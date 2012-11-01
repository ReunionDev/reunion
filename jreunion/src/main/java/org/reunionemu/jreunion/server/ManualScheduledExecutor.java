package org.reunionemu.jreunion.server;

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.*;

/**
 * @author Aidamina
 */
public class ManualScheduledExecutor extends AbstractExecutorService implements
		ScheduledExecutorService {

	/**
	 * An annoying wrapper class to convince javac to use a
	 * DelayQueue<RunnableScheduledFuture> as a BlockingQueue<Runnable>
	 */
	private static class DelayedWorkQueue extends AbstractCollection<Runnable>
			implements BlockingQueue<Runnable> {

		private final DelayQueue<RunnableScheduledFuture<?>> dq = new DelayQueue<RunnableScheduledFuture<?>>();

		public boolean add(Runnable x) {
			return dq.add((RunnableScheduledFuture<?>) x);
		}

		public void clear() {
			dq.clear();
		}

		public boolean contains(Object x) {
			return dq.contains(x);
		}

		public int drainTo(Collection<? super Runnable> c) {
			return dq.drainTo(c);
		}

		public int drainTo(Collection<? super Runnable> c, int maxElements) {
			return dq.drainTo(c, maxElements);
		}

		public Runnable element() {
			return dq.element();
		}

		public boolean isEmpty() {
			return dq.isEmpty();
		}

		public Iterator<Runnable> iterator() {
			return new Iterator<Runnable>() {
				private Iterator<RunnableScheduledFuture<?>> it = dq.iterator();

				public boolean hasNext() {
					return it.hasNext();
				}

				public Runnable next() {
					return it.next();
				}

				public void remove() {
					it.remove();
				}
			};
		}

		public boolean offer(Runnable x) {
			return dq.offer((RunnableScheduledFuture<?>) x);
		}

		public boolean offer(Runnable x, long timeout, TimeUnit unit) {
			return dq.offer((RunnableScheduledFuture<?>) x, timeout, unit);
		}

		public Runnable peek() {
			return dq.peek();
		}

		public Runnable poll() {
			return dq.poll();
		}

		public Runnable poll(long timeout, TimeUnit unit)
				throws InterruptedException {
			return dq.poll(timeout, unit);
		}

		public void put(Runnable x) {
			dq.put((RunnableScheduledFuture<?>) x);
		}

		public int remainingCapacity() {
			return dq.remainingCapacity();
		}

		public Runnable remove() {
			return dq.remove();
		}

		public boolean remove(Object x) {
			return dq.remove(x);
		}

		public int size() {
			return dq.size();
		}

		public Runnable take() throws InterruptedException {
			return dq.take();
		}

		public Object[] toArray() {
			return dq.toArray();
		}

		public <T> T[] toArray(T[] array) {
			return dq.toArray(array);
		}
	}

	private class ScheduledFutureTask<V> extends FutureTask<V> implements
			RunnableScheduledFuture<V> {

		/** Sequence number to break ties FIFO */
		private final long sequenceNumber;
		/** The time the task is enabled to execute in nanoTime units */
		private long time;
		/**
		 * Period in nanoseconds for repeating tasks. A positive value indicates
		 * fixed-rate execution. A negative value indicates fixed-delay
		 * execution. A value of 0 indicates a non-repeating task.
		 */
		private final long period;

		/** The actual task to be re-enqueued by reExecutePeriodic */
		RunnableScheduledFuture<V> outerTask = this;

		/**
		 * Creates a one-shot action with given nanoTime-based trigger.
		 */
		ScheduledFutureTask(Callable<V> callable, long ns) {
			super(callable);
			this.time = ns;
			this.period = 0;
			this.sequenceNumber = sequencer.getAndIncrement();
		}

		/**
		 * Creates a one-shot action with given nanoTime-based trigger time.
		 */
		ScheduledFutureTask(Runnable r, V result, long ns) {
			super(r, result);
			this.time = ns;
			this.period = 0;
			this.sequenceNumber = sequencer.getAndIncrement();
		}

		/**
		 * Creates a periodic action with given nano time and period.
		 */
		ScheduledFutureTask(Runnable r, V result, long ns, long period) {
			super(r, result);
			this.time = ns;
			this.period = period;
			this.sequenceNumber = sequencer.getAndIncrement();
		}

		public int compareTo(Delayed other) {
			if (other == this) // compare zero ONLY if same object
				return 0;
			if (other instanceof ScheduledFutureTask) {
				ScheduledFutureTask<?> x = (ScheduledFutureTask<?>) other;
				long diff = time - x.time;
				if (diff < 0)
					return -1;
				else if (diff > 0)
					return 1;
				else if (sequenceNumber < x.sequenceNumber)
					return -1;
				else
					return 1;
			}
			long d = (getDelay(TimeUnit.NANOSECONDS) - other
					.getDelay(TimeUnit.NANOSECONDS));
			return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
		}

		public long getDelay(TimeUnit unit) {
			long d = unit.convert(time - now(), TimeUnit.NANOSECONDS);
			return d;
		}

		/**
		 * Returns true if this is a periodic (not a one-shot) action.
		 * 
		 * @return true if periodic
		 */
		public boolean isPeriodic() {
			return period != 0;
		}

		/**
		 * Overrides FutureTask version so as to reset/requeue if periodic.
		 */
		public void run() {
			boolean periodic = isPeriodic();
			if (!canRunInCurrentRunState(periodic))
				cancel(false);
			else if (!periodic)
				ScheduledFutureTask.super.run();
			else if (ScheduledFutureTask.super.runAndReset()) {
				setNextRunTime();
				reExecutePeriodic(outerTask);
			}
		}

		/**
		 * Sets the next time to run for a periodic task.
		 */
		private void setNextRunTime() {
			long p = period;
			if (p > 0)
				time += p;
			else
				time = now() - p;
		}
	}

	BlockingQueue<Runnable> workQueue;

	/**
	 * False if should cancel/suppress periodic tasks on shutdown.
	 */
	private volatile boolean continueExistingPeriodicTasksAfterShutdown;

	/**
	 * False if should cancel non-periodic tasks on shutdown.
	 */
	private volatile boolean executeExistingDelayedTasksAfterShutdown = true;

	/**
	 * Sequence number to break scheduling ties, and in turn to guarantee FIFO
	 * order among tied entries.
	 */
	private static final AtomicLong sequencer = new AtomicLong(0);

	private volatile boolean shutdown = false;

	private volatile boolean terminated = false;

	final Lock lock = new ReentrantLock();

	Condition termination = lock.newCondition();

	/**
	 * Creates a new {@code ScheduledThreadPoolExecutor} with the given core
	 * pool size.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are
	 *            idle, unless {@code allowCoreThreadTimeOut} is set
	 * @throws IllegalArgumentException
	 *             if {@code corePoolSize < 0}
	 */
	public ManualScheduledExecutor() {

		workQueue = new DelayedWorkQueue();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		lock.lock();
		try {
			for (;;) {
				if (isTerminated()) {
					return true;
				}
				if (nanos <= 0)
					return false;
				nanos = termination.awaitNanos(nanos);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns true if can run a task given current run state and
	 * run-after-shutdown parameters.
	 * 
	 * @param periodic
	 *            true if this task periodic, false if delayed
	 */
	boolean canRunInCurrentRunState(boolean periodic) {

		boolean shutdownOK = periodic ? continueExistingPeriodicTasksAfterShutdown
				: executeExistingDelayedTasksAfterShutdown;

		return !shutdown || (shutdown && shutdownOK);
	}

	/**
	 * Modifies or replaces the task used to execute a callable. This method can
	 * be used to override the concrete class used for managing internal tasks.
	 * The default implementation simply returns the given task.
	 * 
	 * @param callable
	 *            the submitted Callable
	 * @param task
	 *            the task created to execute the callable
	 * @return a task that can execute the callable
	 * @since 1.6
	 */
	protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable,
			RunnableScheduledFuture<V> task) {
		return task;
	}

	/**
	 * Modifies or replaces the task used to execute a runnable. This method can
	 * be used to override the concrete class used for managing internal tasks.
	 * The default implementation simply returns the given task.
	 * 
	 * @param runnable
	 *            the submitted Runnable
	 * @param task
	 *            the task created to execute the runnable
	 * @return a task that can execute the runnable
	 * @since 1.6
	 */
	protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable,
			RunnableScheduledFuture<V> task) {
		return task;
	}

	/**
	 * Main execution method for delayed or periodic tasks. If pool is shut
	 * down, rejects the task. Otherwise adds task to queue and starts a thread,
	 * if necessary, to run it. (We cannot prestart the thread to run the task
	 * because the task (probably) shouldn't be run yet,) If the pool is shut
	 * down while the task is being added, cancel and remove it if required by
	 * state and run-after-shutdown parameters.
	 * 
	 * @param task
	 *            the task
	 */
	private void delayedExecute(RunnableScheduledFuture<?> task) {
		if (isShutdown()) {
			// reject(task);
		} else {
			getQueue().add(task);
			if (isShutdown() && !canRunInCurrentRunState(task.isPeriodic())
					&& remove(task)) {
				task.cancel(false);
			}
		}
	}

	/**
	 * Executes {@code command} with zero required delay. This has effect
	 * equivalent to {@link #schedule(Runnable,long,TimeUnit) schedule(command,
	 * 0, anyUnit)}. Note that inspections of the queue and of the list returned
	 * by {@code shutdownNow} will access the zero-delayed
	 * {@link ScheduledFuture}, not the {@code command} itself.
	 * 
	 * <p>
	 * A consequence of the use of {@code ScheduledFuture} objects is that
	 * {@link ThreadPoolExecutor#afterExecute afterExecute} is always called
	 * with a null second {@code Throwable} argument, even if the
	 * {@code command} terminated abruptly. Instead, the {@code Throwable}
	 * thrown by such a task can be obtained via {@link Future#get}.
	 * 
	 * @throws RejectedExecutionException
	 *             at discretion of {@code RejectedExecutionHandler}, if the
	 *             task cannot be accepted for execution because the executor
	 *             has been shut down
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 */
	public void execute(Runnable command) {
		schedule(command, 0, TimeUnit.NANOSECONDS);
	}

	/**
	 * Gets the policy on whether to continue executing existing periodic tasks
	 * even when this executor has been {@code shutdown}. In this case, these
	 * tasks will only terminate upon {@code shutdownNow} or after setting the
	 * policy to {@code false} when already shutdown. This value is by default
	 * {@code false}.
	 * 
	 * @return {@code true} if will continue after shutdown
	 * @see #setContinueExistingPeriodicTasksAfterShutdownPolicy
	 */
	public boolean getContinueExistingPeriodicTasksAfterShutdownPolicy() {
		return continueExistingPeriodicTasksAfterShutdown;
	}

	/**
	 * Gets the policy on whether to execute existing delayed tasks even when
	 * this executor has been {@code shutdown}. In this case, these tasks will
	 * only terminate upon {@code shutdownNow}, or after setting the policy to
	 * {@code false} when already shutdown. This value is by default
	 * {@code true}.
	 * 
	 * @return {@code true} if will execute after shutdown
	 * @see #setExecuteExistingDelayedTasksAfterShutdownPolicy
	 */
	public boolean getExecuteExistingDelayedTasksAfterShutdownPolicy() {
		return executeExistingDelayedTasksAfterShutdown;
	}

	// Override AbstractExecutorService methods

	/**
	 * Returns the task queue used by this executor. Each element of this queue
	 * is a {@link ScheduledFuture}, including those tasks submitted using
	 * {@code execute} which are for scheduling purposes used as the basis of a
	 * zero-delay {@code ScheduledFuture}. Iteration over this queue is
	 * <em>not</em> guaranteed to traverse tasks in the order in which they will
	 * execute.
	 * 
	 * @return the task queue
	 */
	public BlockingQueue<Runnable> getQueue() {
		return workQueue;
	}

	@Override
	public boolean isShutdown() {
		return shutdown;
	}

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	/**
	 * Returns current nanosecond time.
	 */
	final long now() {
		return System.nanoTime();
	}

	/**
	 * Cancels and clears the queue of all tasks that should not be run due to
	 * shutdown policy. Invoked within super.shutdown.
	 */

	void onShutdown() {
		BlockingQueue<Runnable> q = getQueue();
		boolean keepDelayed = getExecuteExistingDelayedTasksAfterShutdownPolicy();
		boolean keepPeriodic = getContinueExistingPeriodicTasksAfterShutdownPolicy();
		if (!keepDelayed && !keepPeriodic)
			q.clear();
		else {
			// Traverse snapshot to avoid iterator exceptions
			for (Object e : q.toArray()) {
				if (e instanceof RunnableScheduledFuture) {
					RunnableScheduledFuture<?> t = (RunnableScheduledFuture<?>) e;
					if ((t.isPeriodic() ? !keepPeriodic : !keepDelayed)
							|| t.isCancelled()) { // also remove if already
													// cancelled
						if (q.remove(t))
							t.cancel(false);
					}
				}
			}
		}
	}

	/**
	 * Requeues a periodic task unless current run state precludes it. Same idea
	 * as delayedExecute except drops task rather than rejecting.
	 * 
	 * @param task
	 *            the task
	 */
	void reExecutePeriodic(RunnableScheduledFuture<?> task) {
		if (canRunInCurrentRunState(true)) {
			getQueue().add(task);
			if (!canRunInCurrentRunState(true) && remove(task))
				task.cancel(false);

		}
	}

	public boolean remove(Runnable task) {
		boolean removed = workQueue.remove(task);
		// tryTerminate(); // In case SHUTDOWN and now empty
		return removed;
	}

	public Runnable runNext() {
		Runnable task = getQueue().poll();
		if(task!=null){
			try {
				task.run();
				return task;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return task;
	}

	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay,
			TimeUnit unit) {
		if (callable == null || unit == null)
			throw new NullPointerException();
		if (delay < 0)
			delay = 0;
		long triggerTime = now() + unit.toNanos(delay);
		RunnableScheduledFuture<V> t = decorateTask(callable,
				new ScheduledFutureTask<V>(callable, triggerTime));
		delayedExecute(t);
		return t;
	}

	public ScheduledFuture<?> schedule(Runnable command, long delay,
			TimeUnit unit) {
		if (command == null || unit == null)
			throw new NullPointerException();
		if (delay < 0)
			delay = 0;
		long triggerTime = now() + unit.toNanos(delay);
		RunnableScheduledFuture<?> t = decorateTask(command,
				new ScheduledFutureTask<Void>(command, null, triggerTime));
		delayedExecute(t);
		return t;
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
			long initialDelay, long period, TimeUnit unit) {
		if (command == null || unit == null)
			throw new NullPointerException();
		if (period <= 0)
			throw new IllegalArgumentException();
		if (initialDelay < 0)
			initialDelay = 0;
		long triggerTime = now() + unit.toNanos(initialDelay);
		ScheduledFutureTask<Void> sft = new ScheduledFutureTask<Void>(command,
				null, triggerTime, unit.toNanos(period));
		RunnableScheduledFuture<Void> t = decorateTask(command, sft);
		sft.outerTask = t;
		delayedExecute(t);
		return t;
	}

	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
			long initialDelay, long delay, TimeUnit unit) {
		if (command == null || unit == null)
			throw new NullPointerException();
		if (delay <= 0)
			throw new IllegalArgumentException();
		if (initialDelay < 0)
			initialDelay = 0;
		long triggerTime = now() + unit.toNanos(initialDelay);
		ScheduledFutureTask<Void> sft = new ScheduledFutureTask<Void>(command,
				null, triggerTime, unit.toNanos(-delay));
		RunnableScheduledFuture<Void> t = decorateTask(command, sft);
		sft.outerTask = t;
		delayedExecute(t);
		return t;
	}

	/**
	 * Sets the policy on whether to continue executing existing periodic tasks
	 * even when this executor has been {@code shutdown}. In this case, these
	 * tasks will only terminate upon {@code shutdownNow} or after setting the
	 * policy to {@code false} when already shutdown. This value is by default
	 * {@code false}.
	 * 
	 * @param value
	 *            if {@code true}, continue after shutdown, else don't.
	 * @see #getContinueExistingPeriodicTasksAfterShutdownPolicy
	 */
	public void setContinueExistingPeriodicTasksAfterShutdownPolicy(
			boolean value) {
		continueExistingPeriodicTasksAfterShutdown = value;
		if (!value && isShutdown())
			onShutdown();
	}

	/**
	 * Sets the policy on whether to execute existing delayed tasks even when
	 * this executor has been {@code shutdown}. In this case, these tasks will
	 * only terminate upon {@code shutdownNow}, or after setting the policy to
	 * {@code false} when already shutdown. This value is by default
	 * {@code true}.
	 * 
	 * @param value
	 *            if {@code true}, execute after shutdown, else don't.
	 * @see #getExecuteExistingDelayedTasksAfterShutdownPolicy
	 */
	public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean value) {
		executeExistingDelayedTasksAfterShutdown = value;
		if (!value && isShutdown())
			onShutdown();
	}

	@Override
	public void shutdown() {

		lock.lock();
		try {
			shutdown = true;
			onShutdown();
			while (runNext() != null)
				;
			terminated = true;
			termination.signalAll();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public List<Runnable> shutdownNow() {

		lock.lock();
		try {
			shutdown = true;
			return Arrays.asList(workQueue.toArray(new Runnable[workQueue
					.size()]));
		} finally {
			terminated = true;
			lock.unlock();
		}
	}

	public <T> Future<T> submit(Callable<T> task) {
		return schedule(task, 0, TimeUnit.NANOSECONDS);
	}

	public Future<?> submit(Runnable task) {
		return schedule(task, 0, TimeUnit.NANOSECONDS);
	}

	public <T> Future<T> submit(Runnable task, T result) {
		return schedule(Executors.callable(task, result), 0,
				TimeUnit.NANOSECONDS);
	}
}
