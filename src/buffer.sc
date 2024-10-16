import('extensions', 'all_extensions_functions');
for(all_extensions_functions(), import('extensions', _));

import('encoder', 'all_encoder_functions');
for(all_encoder_functions(), import('encoder', _));

global_DIMENSIONS = ['minecraft:overworld', 'minecraft:the_nether', 'minecraft:the_end'];
global_MAX_SIZE = 48*48*27;
global_offset = 0;
global_dimensionMinY = -64;
global_dimension = 'minecraft:overworld';

init(... dimension) -> (
    [dimension] = __values_and_defaults(dimension, 'minecraft:overworld');

    if((global_DIMENSIONS ~ dimension) == null,
        print(format('r '+dimension+' is not a vaild dimension.'));
        return();
    );

    if(dimension == 'minecraft:the_end',
        print(format('r '+dimension+' is not a supported dimension.'));
        return();
    );

    global_dimensionMinY = in_dimension(dimension, system_info('world_bottom'));
    global_dimension = dimension;

    if(block(0, global_dimensionMinY, 0) != 'air' && block(0, global_dimensionMinY, 0) != 'light_gray_shulker_box',
        in_dimension(dimension, run('forceload add 0 0 2 2'));
        in_dimension(dimension, run('fill 0 '+global_dimensionMinY+' 0 47 '+global_dimensionMinY+' 47 air'));
        in_dimension(dimension, run('fill 0 '+(global_dimensionMinY + 1)+' 0 47 '+(global_dimensionMinY + 1)+' 47 bedrock'));
    );
);

MAX_SIZE() -> (
    return(global_MAX_SIZE);
);

clear() -> (
    run('fill 0 '+global_dimensionMinY+' 0 47 '+global_dimensionMinY+' 47 air');
);

get_dimension() -> (
    return(global_dimension);
);

get_offset() -> (
    return(global_offset);
);

get_offset_location(... offset) -> (
    [offset] = __values_and_defaults(offset, global_offset);

    block_offset = floor(offset / 27);

    block_x = floor(block_offset / 48);
    block_z = block_offset % 48;

    block_slot = offset % 27;

    return([block_x, block_z, block_slot]);
);

set_offset(offset) -> (
    if(offset < 0 && offset > global_MAX_SIZE,
        print(format('r Invaild offset. Must be between 0 and '+global_MAX_SIZE));
        return();
    );
    global_offset = offset;
);

read_boolean(... offset) -> (
    [offset] = __values_and_defaults(offset, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = _read(offset);
    global_offset += 1;
    return(value == 1);
);

read_ubyte(... offset) -> (
    [offset] = __values_and_defaults(offset, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = _read(offset);
    global_offset += 1;
    return(if(value < 0, value + 2^8, value));
);

read_byte(... offset) -> (
    [offset] = __values_and_defaults(offset, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = _read(offset);
    global_offset += 1;
    return(if(value < -(2^8 / 2) || value > (2^8 / 2) - 1, value - 2^8, value));
);

read_ushort(... littleEndian_offset) -> (
    [little_endian, offset] = __values_and_defaults(littleEndian_offset, false, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = if(little_endian,
        bitwise_or(bitwise_and(_read(offset), 0xFF), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 8));
    ,
        bitwise_or(bitwise_shift_left(bitwise_and(_read(offset), 0xFF), 8), bitwise_and(_read(offset + 1), 0xFF));
    );

    global_offset += 2;
    return(if(value < 0, value + 2^16, value));
);

read_short(... littleEndian_offset) -> (
    [little_endian, offset] = __values_and_defaults(littleEndian_offset, false, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = if(little_endian,
        bitwise_or(bitwise_and(_read(offset), 0xFF), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 8));
    ,
        bitwise_or(bitwise_shift_left(bitwise_and(_read(offset), 0xFF), 8), bitwise_and(_read(offset + 1), 0xFF));
    );

    global_offset += 2;
    return(if(value < -(2^16 / 2) || value > (2^16 / 2) - 1, value - 2^16, value));
);

read_uint(... littleEndian_offset) -> (
    [little_endian, offset] = __values_and_defaults(littleEndian_offset, false, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = if(little_endian,
        bitwise_or(bitwise_or(bitwise_or(bitwise_and(_read(offset), 0xFF), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 8)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 3), 0xFF), 24));
    ,
        bitwise_or(bitwise_or(bitwise_or(bitwise_shift_left(bitwise_and(_read(offset), 0xFF), 24), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 8)), bitwise_and(_read(offset + 3), 0xFF));
    );

    global_offset += 4;
    return(if(value < 0, value + 2^32, value));
);

read_int(... littleEndian_offset) -> (
    [little_endian, offset] = __values_and_defaults(littleEndian_offset, false, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = if(little_endian,
        bitwise_or(bitwise_or(bitwise_or(bitwise_and(_read(offset), 0xFF), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 8)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 3), 0xFF), 24));
    ,
        bitwise_or(bitwise_or(bitwise_or(bitwise_shift_left(bitwise_and(_read(offset), 0xFF), 24), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 8)), bitwise_and(_read(offset + 3), 0xFF));
    );

    global_offset += 4;
    return(if(value < -(2^32 / 2) || value > (2^32 / 2) - 1, value - 2^32, value));
);

read_ulong(... littleEndian_offset) -> (
    [little_endian, offset] = __values_and_defaults(littleEndian_offset, false, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = if(little_endian,
        bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_and(_read(offset), 0xFF), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 8)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 3), 0xFF), 24)), bitwise_shift_left(bitwise_and(_read(offset + 4), 0xFF), 32)), bitwise_shift_left(bitwise_and(_read(offset + 5), 0xFF), 40)), bitwise_shift_left(bitwise_and(_read(offset + 6), 0xFF), 48)), bitwise_shift_left(bitwise_and(_read(offset + 7), 0xFF), 56));
    ,
        bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_shift_left(bitwise_and(_read(offset), 0xFF), 56), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 48)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 40)), bitwise_shift_left(bitwise_and(_read(offset + 3), 0xFF), 32)), bitwise_shift_left(bitwise_and(_read(offset + 4), 0xFF), 24)), bitwise_shift_left(bitwise_and(_read(offset + 5), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 6), 0xFF), 8)), bitwise_and(_read(offset + 7), 0xFF));
    );

    global_offset += 8;
    return(if(value < 0, value + 2^64, value));
);

read_long(... littleEndian_offset) -> (
    [little_endian, offset] = __values_and_defaults(littleEndian_offset, false, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = if(little_endian,
        bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_and(_read(offset), 0xFF), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 8)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 3), 0xFF), 24)), bitwise_shift_left(bitwise_and(_read(offset + 4), 0xFF), 32)), bitwise_shift_left(bitwise_and(_read(offset + 5), 0xFF), 40)), bitwise_shift_left(bitwise_and(_read(offset + 6), 0xFF), 48)), bitwise_shift_left(bitwise_and(_read(offset + 7), 0xFF), 56));
    ,
        bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_shift_left(bitwise_and(_read(offset), 0xFF), 56), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 48)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 40)), bitwise_shift_left(bitwise_and(_read(offset + 3), 0xFF), 32)), bitwise_shift_left(bitwise_and(_read(offset + 4), 0xFF), 24)), bitwise_shift_left(bitwise_and(_read(offset + 5), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 6), 0xFF), 8)), bitwise_and(_read(offset + 7), 0xFF));
    );

    global_offset += 8;
    return(if(value < -(2^64 / 2) || value > (2^64 / 2) - 1, value - 2^64, value));
);

read_float(... littleEndian_offset) -> (
    [little_endian, offset] = __values_and_defaults(littleEndian_offset, false, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = if(little_endian,
        bitwise_or(bitwise_or(bitwise_or(bitwise_and(_read(offset), 0xFF), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 8)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 3), 0xFF), 24));
    ,
        bitwise_or(bitwise_or(bitwise_or(bitwise_shift_left(bitwise_and(_read(offset), 0xFF), 24), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 8)), bitwise_and(_read(offset + 3), 0xFF));
    );

    global_offset += 4;
    return(int_to_float_bits(value));
);

read_double(... littleEndian_offset) -> (
    [little_endian, offset] = __values_and_defaults(littleEndian_offset, false, global_offset);

    if(length(offset) > 0,
        global_offset = offset;
    );

    value = if(little_endian,
        bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_and(_read(offset), 0xFF), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 8)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 3), 0xFF), 24)), bitwise_shift_left(bitwise_and(_read(offset + 4), 0xFF), 32)), bitwise_shift_left(bitwise_and(_read(offset + 5), 0xFF), 40)), bitwise_shift_left(bitwise_and(_read(offset + 6), 0xFF), 48)), bitwise_shift_left(bitwise_and(_read(offset + 7), 0xFF), 56));
    ,
        bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_or(bitwise_shift_left(bitwise_and(_read(offset), 0xFF), 56), bitwise_shift_left(bitwise_and(_read(offset + 1), 0xFF), 48)), bitwise_shift_left(bitwise_and(_read(offset + 2), 0xFF), 40)), bitwise_shift_left(bitwise_and(_read(offset + 3), 0xFF), 32)), bitwise_shift_left(bitwise_and(_read(offset + 4), 0xFF), 24)), bitwise_shift_left(bitwise_and(_read(offset + 5), 0xFF), 16)), bitwise_shift_left(bitwise_and(_read(offset + 6), 0xFF), 8)), bitwise_and(_read(offset + 7), 0xFF));
    );

    global_offset += 8;
    return(long_to_double_bits(value));
);

read_string(... charSet_littleEndian_offset) -> (
    [charSet, little_endian, offset] = __values_and_defaults(charSet_littleEndian_offset, 'utf-8', false, global_offset);

    if(length(charSet_littleEndian_offset) > 2,
        global_offset = offset;
    );

    len = read_ushort(little_endian, offset);
    offset += 2;

    str_bytes = [];
    c_for(i = 0, i < len, i += 1,
        put(str_bytes, i, _read(offset + i));
    );

    encoder_init(charSet);
    value = decode(str_bytes, little_endian);

    global_offset += length(value);
    return(value);
);

_read(... offset) -> (
    [offset] = __values_and_defaults(offset, global_offset);

    [block_x, block_z, block_slot] = get_offset_location(offset);

    data_block = in_dimension(global_dimension, block(block_x, global_dimensionMinY, block_z));

    if(data_block != 'light_gray_shulker_box',
        print(format('r Offset out of bounds. Nothing to read at: '+ global_offset));
        return();
    );

    data_slot = inventory_get(pos(data_block), block_slot);

    if(data_slot:0 == 'tinted_glass',
        return(0);
    , if(data_slot:0 == 'white_stained_glass',
        return(data_slot:1);
    , if(data_slot:0 == 'light_gray_stained_glass',
        return(data_slot:1 + 64);
    , if(data_slot:0 == 'gray_stained_glass',
        return(data_slot:1 + 128);
    , if(data_slot:0 == 'black_stained_glass',
        return(data_slot:1 + 192);
    ,
        print(format('r Unknown data item type: '+data_slot:0));
        return();
    )))));
);

_write(value, ... offset) -> (
    [offset] = __values_and_defaults(offset, global_offset);

    [block_x, block_z, block_slot] = get_offset_location(offset);

    data_block = in_dimension(global_dimension, block(block_x, global_dimensionMinY, block_z));

    if(data_block:0 != 'light_gray_shulker_box',
        set(pos(data_block), 'light_gray_shulker_box[facing="down"]');
    );

    if(value == 0,
        inventory_set(pos(data_block), block_slot, 1, 'tinted_glass');
    , if(value >= 1 && value <= 64,
        inventory_set(pos(data_block), block_slot, value, 'white_stained_glass');
    , if(value >= 65 && value <= 128,
        inventory_set(pos(data_block), block_slot, value - 64, 'light_gray_stained_glass');
    , if(value >= 129 && value <= 192,
        inventory_set(pos(data_block), block_slot, value - 128, 'gray_stained_glass');
    , if(value >= 193 && value <= 255,
        inventory_set(pos(data_block), block_slot, value - 192, 'black_stained_glass');
    )))));
);


all_functions() -> (
    return([
        'init', 'MAX_SIZE', 'clear', 'get_dimension', 'get_offset', 'get_offset_location', 'set_offset',
        'read_boolean', 'read_ubyte', 'read_byte', 'read_ushort', 'read_short', 'read_uint', 'read_int',
        'read_ulong', 'read_long', 'read_float', 'read_double', 'read_string'
    ]);
);

__config() -> {
    'scope' -> 'global'
};
