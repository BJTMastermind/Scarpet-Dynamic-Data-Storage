# Scarpet Dynamic Data Storage

A library that allows you to read and write data into a multiple shulker box based buffer for use with the Carpet Mod's Scarpet language.

This is a Minecraft: Java Edition counterpart to [MCBE-Dynamic-Data-Storage](https://github.com/BJTMastermind/MCBE-Dynamic-Data-Storage/edit/main/README.md) to allow access to data from a converted bedrock world.

## How To Use

Coming Soon

<!--
***Stable***

```js
// creating buffer instance
let buffer = new Buffer();

// reading
buffer.readBoolean();
buffer.readUByte();
buffer.readByte();
buffer.readUShort();
buffer.readShort();
buffer.readUInt();
buffer.readInt();
buffer.readULong();
buffer.readLong();
buffer.readFloat();
buffer.readDouble();
buffer.readString();

// writing
buffer.writeBoolean();
buffer.writeUByte();
buffer.writeByte();
buffer.writeUShort();
buffer.writeShort();
buffer.writeUInt();
buffer.writeInt();
buffer.writeULong();
buffer.writeLong();
buffer.writeFloat();
buffer.writeDouble();
buffer.writeString();

// other
buffer.MAX_SIZE;
buffer.clear();
buffer.getDimension();
buffer.getOffset();
buffer.getOffsetLocation();
buffer.setOffset();
```

***Experimental***

```js
buffer.close();
buffer.delete();
buffer.save();
buffer.load();
```

All methods have doc strings for extra details about how to use them and what they are for.

***For Devs***

All data written from this library is stored in a 48x48 area under X0,Z0 at the bottom of the world in the dimension it was told to be saved in. (Defaults to the overworld)
-->

## Language(s) Used

* Scarpet 1.4.147 for MC 1.21.1
