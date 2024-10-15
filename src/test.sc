import('buffer', 'all_functions');
for(all_functions(), import('buffer', _));

read_test() -> (
    init();

    header = read_byte();

    if(header != 20,
        print(format('r Invaild header. Expected 20 got '+header));
        return();
    );

    len = read_short();

    c_for(i = 0, i < len, i += 1,
        x = read_int();
        y = read_int();
        z = read_int();
        print([x, y, z]);
    );
);

clear_test() -> (
    clear();
);