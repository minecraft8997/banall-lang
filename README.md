# The BanAll Programming Language<br><sup>Version 0.1.0a</sup>

# TODO: 

- Write a comprehensive description
- Make more examples.

# Download
There's two options, either running `mvn package` or downloading the [latest artifact from CI](https://nightly.link/minecraft8997/banall-lang/workflows/build/master/builds.zip).

# Hints and Tips for Developers

**Things you should consider before starting coding in this language:**
1.  The current version does not support variables or fields,
    maybe this feature will be added in future releases;
2.  The only return type which functions currently support is `void`;
3.  The language does support exceptions. They can be thrown by calling
    `throw_banall("<class>", "<detailed message>");`
4.  You can return either nothing (just by calling `return`) or a
    `tempban` followed by duration parameter (see example programs).
    Note that returning a tempban anywhere outside `main()` function
    is useless (at least until I add variables and fields, so it becomes possible
    to record return value);
5.  If the interpreter receives anything except a `tempban` when
    execution of `main()` method finishes (empty return value/
    exception/Java exception etc.) it will ban itself for 1 week. The same
    thing will happen if interpreter faces with a `ParseError`;
6.  If the interpreter receives a `tempban` it will ban itself
    for a period that you specified in the function parameters.

# Standard Library functions

```
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
```

Check out example programs in the "examples" folder!
