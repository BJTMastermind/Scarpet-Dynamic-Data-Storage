import('bmp_mapping_char_to_num', 'global_BMP_MAPPINGS_CHAR_TO_NUM');
import('bmp_mapping_num_to_char', 'global_BMP_MAPPINGS_NUM_TO_CHAR');

all_extensions_functions() -> (
    return(['int_to_float_bits', 'float_to_int_bits', 'code_point_at', 'from_code_point', 'char_code_at', 'from_char_code', '__values_and_defaults']);
);

int_to_float_bits(value) -> (
    if(value < 0 || value > 0xFFFFFFFF,
        print(format('r Input must be a 32-bit unsigned integer.'));
        return();
    );

    sign = bitwise_and(bitwise_shift_right(value, 31), 0x01);
    exponent = bitwise_and(bitwise_shift_right(value, 23), 0xFF);
    mantissa = bitwise_and(value, 0x7FFFFF);

    if(exponent == 0 && mantissa == 0,
        // Zero case
        return(0.0);
    , if(exponent == 0xFF,
        if(mantissa != 0,
            // NaN case
            return('NaN');
        ,
            // Infinity case
            if(sign == 0, return('Infinity'), return('-Infinity'));
        );
    ));

    // Normalize exponent
    exponent = exponent - 127;

    float_value = (-1) ^ sign * (1 + mantissa / bitwise_shift_left(1, 23)) * (2 ^ exponent);
    return(float_value);
);

float_to_int_bits(value) -> (
    if(value == 0.0,
        // Zero case
        return(0);
    , if(value != value,
        // NaN case
        return(0x7FFFFFFF);
    , if(value == 'Infinity',
        // Infinity case
        return(0x7F800000);
    , if(value == '-Infinity',
        // -Infinity case
        return(0xFF800000);
    ))));

    // Extract the sign, exponent, and mantissa
    sign = 0;
    if(value < 0,
        sign = 1;
        value = -value;
    );

    // Normalize the value
    exponent = 0;
    while(value >= 2.0,
        value = value / 2.0;
        exponent += 1;
    );
    while(value < 1.0 && value > 0.0,
        value = value * 2.0;
        exponent = exponent - 1;
    );

    // Adjust for bias (127)
    exponent += 127;

    // Construct the mantissa
    mantissa = bitwise_and(number((value - 1) * bitwise_shift_left(1, 23)), 0x7FFFFF);

    int_value = bitwise_or(bitwise_shift_left(sign, 31), bitwise_or(bitwise_shift_left(exponent, 23), mantissa));
    return(int_value);
);

code_point_at(string, pos) -> (
    size = length(string);

    if(pos < 0 || pos >= size,
        return();
    );

    first = char_code_at(string, pos);
    if(first < 0xD800 || first > 0xDBFF || pos + 1 == size,
        return(first);
    );

    second = char_code_at(string, pos);
    if(second < 0xDC00 || second > 0xDFFF,
        return(first);
    );

    return((first - 0xD800) * 0x400 + second + 0x2400);
);

from_code_point(byte) -> (
    result = '';

    if(byte > 0xFFFF,
        byte = byte - 0x10000;
        highSurrogate = bitwise_shift_right(byte, 10) + 0xD800;
        lowSurrogate = bitwise_and(byte, 0x3FF) + 0xDC00;
        result += from_char_code(highSurrogate) + from_char_code(lowSurrogate);
    ,
        result += from_char_code(byte);
    );

    return(result);
);

char_code_at(string, pos) -> (
    if(pos < 0 || pos >= length(string),
        return('NaN');
    );

    char = slice(string, pos, pos + 1);
    return(get(global_BMP_MAPPINGS_CHAR_TO_NUM, char));
);

from_char_code(byte) -> (
    return(get(global_BMP_MAPPINGS_NUM_TO_CHAR, byte));
);

__values_and_defaults(arguments, ... defaults) -> (
    c_for(i = length(arguments), i < length(defaults), i += 1,
        put(arguments, i, get(defaults, i));
    );
    return(arguments);
);
