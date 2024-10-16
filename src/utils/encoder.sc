import('extensions', 'all_extensions_functions');
for(all_extensions_functions(), import('extensions', _));

global_CHARSETS = ['utf-8', 'utf-16'];
global_charSet = 'utf-8';

encoder_init(... charSet) -> (
    [charSet] = __values_and_defaults(charSet, 'utf-8');

    if((global_CHARSETS ~ charSet) != null,
        global_charSet = charSet;
    ,
        print(format('r '+charSet+' Is not a vaild CharSet.'));
        return();
    );
);

get_charSet() -> (
    return(global_charSet);
);

encode(string, ... little_endian) -> (
    [little_endian] = __values_and_defaults(little_endian, false);

    if(global_charSet == 'utf-8',
        return(__encode_utf8(string));
    , if (global_charSet == 'utf-16',
        return(__encode_utf16(string, little_endian));
    ));
);

decode(bytes, ... little_endian) -> (
    [little_endian] = __values_and_defaults(little_endian, false);

    if(global_charSet == 'utf-8',
        return(__decode_utf8(bytes));
    , if (global_charSet == 'utf-16',
        return(__decode_utf16(bytes, little_endian));
    ));
);

////////////////////////////////////////
////////// Encoding Functions //////////
////////////////////////////////////////

__encode_utf8(string) -> (
    bytes = [];

    c_for(i = 0, i < length(string), i += 1,
        code_point = code_point_at(string, i);

        // Skip surrogate pairs
        if(code_point > 0xFFFF,
            i += 1;
        );

        // 1 byte character
        if(code_point <= 0x7F,
            put(bytes, length(bytes), code_point);
            continue();
        );

        // 2 byte character
        if(code_point <= 0x7FF,
            put(bytes, length(bytes), bitwise_or(0xC0, bitwise_shift_right(code_point, 6)));
            put(bytes, length(bytes), bitwise_or(0x80, bitwise_and(code_point, 0x3F)));
            continue();
        );

        // 3 byte character
        if(code_point <= 0xFFFF,
            put(bytes, length(bytes), bitwise_or(0xE0, bitwise_shift_right(code_point, 12)));
            put(bytes, length(bytes), bitwise_or(0x80, bitwise_and(bitwise_shift_right(code_point, 6), 0x3F)));
            put(bytes, length(bytes), bitwise_or(0x80, bitwise_and(code_point, 0x3F)));
            continue();
        );

        // 4 byte character
        put(bytes, length(bytes), bitwise_or(0xF0, bitwise_shift_right(code_point, 18)));
        put(bytes, length(bytes), bitwise_or(0x80, bitwise_and(bitwise_shift_right(code_point, 12), 0x3F)));
        put(bytes, length(bytes), bitwise_or(0x80, bitwise_and(bitwise_shift_right(code_point, 6), 0x3F)));
        put(bytes, length(bytes), bitwise_or(0x80, bitwise_shift_right(code_point, 0x3F)));
    );

    result = __concat_array(__number_to_bytes(length(bytes), 2), bytes);

    return(result);
);

__encode_utf16(string, ... little_endian) -> (
    [little_endian] = __values_and_defaults(little_endian, false);

    _
);

////////////////////////////////////////
////////// Decoding Functions //////////
////////////////////////////////////////

__decode_utf8(bytes) -> (
    string = '';

    c_for(i = 0, i < length(bytes), i += 1,
        byte = bytes:i;

        // 1 byte character
        if(byte <= 0x7F,
            string += from_code_point(byte);
            continue();
        );

        // 2 byte character
        if(bitwise_and(byte, 0xE0) == 0xC0,
            byte1 = bitwise_and(byte, 0x1F);
            i += 1;
            byte2 = bytes:i;
            code_point = bitwise_or(bitwise_shift_left(byte1, 6), byte2);

            string += from_code_point(code_point);
            continue();
        );

        // 3 byte character
        if(bitwise_and(byte, 0xF0) == 0xE0,
            byte1 = bitwise_and(byte, 0x07);
            i += 1;
            byte2 = bytes:i;
            i += 1;
            byte3 = bytes:i;
            code_point = bitwise_or(bitwise_shift_left(byte1, 12), bitwise_or(bitwise_shift_left(byte2, 6), byte3));

            string += from_code_point(code_point);
            continue();
        );

        // 4 byte character
        if(bitwise_and(byte, 0xF8) == 0xF0,
            byte1 = bitwise_and(byte, 0x07);
            i += 1;
            byte2 = bytes:i;
            i += 1;
            byte3 = bytes:i;
            i += 1;
            byte4 = bytes:i;
            code_point = bitwise_or(bitwise_shift_left(byte1, 18), bitwise_or(bitwise_shift_left(byte2, 12), bitwise_or(bitwise_shift_left(byte3, 6), byte4)));

            string += from_code_point(code_point);
            continue();
        );
    );

    return(string);
);

__decode_utf16(bytes, ... little_endian) -> (
    [little_endian] = __values_and_defaults(little_endian, false);

    _
);

//////////////////////////////////////
////////// Helper Functions //////////
//////////////////////////////////////

__number_to_bytes(value, byteCount, ... little_endian) -> (
    [little_endian] = __values_and_defaults(little_endian, false);

    result = [];
    c_for(i = 0, i < byteCount, i += 1,
        byte = if(little_endian,
            bitwise_and(bitwise_shift_right(value, (i * 8)), 0xFF);
        ,
            bitwise_and(bitwise_shift_right(value, ((byteCount - 1 - i) * 8)), 0xFF);
        );

        put(result, length(result), byte);
    );
    return(result);
);

__concat_array(array1, array2) -> (
    result = [];
    c_for(i = 0, i < (length(array1) + length(array2)), i += 1,
        put(result, length(result), if(i < length(array1), get(array1, i), get(array2, i - length(array1))));
    );
    return(result);
);

all_encoder_functions() -> (
    return(['encoder_init', 'get_charSet', 'encode', 'decode']);
);