package com.ocronite.pillapilla;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;

class Network {
	static final float tick = 1f/20;
	
	static void registerPackets(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
        kryo.register(boolean.class);
		kryo.register(int.class);
		kryo.register(int[].class);
		kryo.register(float.class);
		kryo.register(String.class);
        kryo.register(IpsPacket.class);
		kryo.register(NewPlayerPacket.class);
        kryo.register(ReadyPacket.class);
		kryo.register(PlayerDisconnectedPacket.class);
		kryo.register(GameOptionsPacket.class);
		kryo.register(TodosVsTodosGameOptionsPacket.class);
		kryo.register(ZombiesGameOptionsPacket.class);
		kryo.register(LoadGameScreenPacket.class);
		kryo.register(StartGamePacket.class);
		kryo.register(PositionPacket.class);
		kryo.register(TeleportPositionPacket.class);
		kryo.register(ResetTagsPacket.class);
		kryo.register(SpawnDropPacket.class);
		kryo.register(DropPickedPacket.class);
		kryo.register(ItemGainedPacket.class);
        kryo.register(ItemLostPacket.class);
		kryo.register(ItemUsedPacket.class);
        kryo.register(AddBuffPacket.class);
        kryo.register(AddGlobalBuffPacket.class);
        kryo.register(RemoveBuffPacket.class);
        kryo.register(RemoveGlobalBuffPacket.class);
        kryo.register(SetVidasPacket.class);
		kryo.register(PjTaggedPacket.class);
		kryo.register(PjDeadPacket.class);
        kryo.register(SetExorcistTarget.class);
		kryo.register(WinnerPacket.class);
		kryo.register(GoBackToLobbyPacket.class);
	}
	
	// Lobby packets

	public static class IpsPacket {
		String localIp;
		String publicIp;
	}
	
	public static class NewPlayerPacket {
		String name;
		int pj_index;
	}

	public static class ReadyPacket {
		int pj_index;
        boolean ready;
	}
	
	public static class PlayerDisconnectedPacket {
		int pj_index;
	}
	
	public static class GameOptionsPacket {
        String gameMode;
		String map;
        String spawnItemsRate;
		String availableItemsMode;
	}
	
	public static class TodosVsTodosGameOptionsPacket extends GameOptionsPacket {
		String objectiveMode;
		String objective;
		String resetTargetsTime;
	}
	
	public static class ZombiesGameOptionsPacket extends GameOptionsPacket {
	    String duration;
	}
	
	public static class LoadGameScreenPacket {}
	
	// Game packets

	public static class StartGamePacket {}
	
	public static class PositionPacket {
		int pj_index;
		float x, y;
		float dirX, dirY;
	}
	
	public static class TeleportPositionPacket extends PositionPacket {}
	
	public static class ResetTagsPacket {
		int[] target, flee;
	}
	
	public static class SpawnDropPacket {
		int itemID;
		float x, y;
	}
	
	public static class DropPickedPacket {
		int index;
	}
	
	public static class ItemGainedPacket {
		int itemID;
		int pj_index;
	}

	public static class ItemLostPacket {
        int pj_index;
    }
	
	public static class ItemUsedPacket {}
	
	public static class AddBuffPacket {
		int pj_index;
		int buff_index;
	}

	public static class AddGlobalBuffPacket extends AddBuffPacket {}
	
	public static class RemoveBuffPacket {
		int pj_index;
		int buff_index;
	}

	public static class RemoveGlobalBuffPacket extends RemoveBuffPacket {}

	public static class SetVidasPacket {
	    int pj_index;
        int vidas;
    }
	
	public static class PjTaggedPacket extends SetVidasPacket {
		int killer_pj_index;
        int kills;
	}
	
	public static class PjDeadPacket extends PjTaggedPacket {}

	public static class SetExorcistTarget {
	    int exorcist_index;
		int pj_index;
	}
	
	public static class WinnerPacket {
		String pj_name;
	}

	public static class GoBackToLobbyPacket {}
}

class GameConnection extends Connection {
	String name;
    boolean ready;
}
