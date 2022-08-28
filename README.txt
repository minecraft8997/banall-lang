-----------------------------------------------
        The BanAll Programming Language
        v0.1.0a
-----------------------------------------------
TODO: Write a comprehensive description

Things you should consider before starting coding in this language:
1.  The current version does not support variables or fields,
    maybe this feature will be added in future releases;
2.  The only return type which functions currently support is <code>void</code>;
3.  The language does support exceptions. They can be thrown by calling
    <code>throw_banall("<class>", "<detailed message>")</code>;
4.  You can return either nothing (just by calling <code>return</code>) or a
    <code>tempban</code> followed by duration parameter (see example programs).
    Note that returning a tempban anywhere outside <code>main()</code> function
    is useless (at least until I add variables and fields, so it becomes possible
    to record return value);
5.  If the interpreter receives anything except a <code>tempban</code> when
    execution of <code>main()</code> method finishes (empty return value/
    exception/Java exception etc.) it will ban itself for 1 week. The same
    thing will happen if interpreter faces with a <code>ParseError</code>;
6.  If the interpreter receives a <code>tempban</code> it will ban itself
    for a period that you specified in the function parameters.

Standard Library functions:
    banall()                                - Simulates behaviour of /banall command presented in ClassiCube's NB server;
    banall_thread(function, thread_name)    - Creates a new thread and invokes the specified function in that thread;
    banall_sleep(duration)                  - Makes the current thread wait the specified amount of
                                              time (in milliseconds);
    banall_println(message)                 - Adds an OS-dependent line separator to the specified message and
                                              then transmits the result to the print() function;
    banall_printls()                        - Prints a line separator;
    banall_print(message)                   - Prints specified message to the console;
    throw_banall(class, message)            - Throws an exception;
    switch_character(name)                  - Switches the current character;
    ping_pandito_in_bryce_request_channel()
    steal_ned_emoji()

Check out example programs in the "examples" folder! TODO: make more examples.
