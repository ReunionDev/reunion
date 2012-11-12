package org.reunionemu.jreunion.protocol.packets.client;

import java.util.*;

import netty.Packet;

public class UseSkillPacket implements Packet {

	private static final long serialVersionUID = 1L;

	int skillId;

	TargetType targetType;

	Integer targetId;

	List<String> arguments = new LinkedList<String>();

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public Integer getTargetId() {
		return targetId;
	}

	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("use_skill ");
		builder.append(getSkillId());

		if (getTargetType() != null) {
			builder.append(' ');
			if (getTargetType() == TargetType.NPC) {
				builder.append("npc");
			} else if (getTargetType() == TargetType.CHAR) {
				builder.append("char");
			}
		}
		if (targetId != null) {
			builder.append(' ');
			builder.append(targetId);
		}
		for (String arg : arguments) {
			builder.append(' ');
			builder.append(arg);
		}
		return builder.toString();
	}

	public enum TargetType {
		NPC, CHAR
	}
}
