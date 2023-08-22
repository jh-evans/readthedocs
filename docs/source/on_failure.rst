On Failure
==========

Programs + Resources = Failure. All resources are external to your program, even the memory, and an attempt to access a resource may fail. Writing to a file can fail as the disk might be full. Reading a
file might error as its permissions have been made too restrictive. And creating a list may fail as there is not enough memory.

The likelihood of some errors is low enough that for all practical purposes, they are treated as if they will not occur. In a programming language, when allocating the next value, it is possible to run out of
memory. Do you write code to explicitly handle this out-of-memory case? No, of course not, no one does that.

When considering code failure, it is useful to distinguish between errors that cannot occur from those that will not occur. The former can be proved, the latter is an opinion.

In a statically typed programming language, a value of type integer can only be used as an integer and any attempt to do differently results in a compilation error. Therefore, errors that might otherwise
arise because of an attempt to use the integer as if it was another type *cannot* occur. The compiler prevents all attempts.

When you write code to put a file onto disk, you are likely to make sure there is enough space to accommodate it. Your goal is to write code to successfully persist data. What do you need to help you
achieve that goal? Enough disk space. However, available space is an assumption that eventually will be false. The disk where you are writing the data can, at any point, be too full to accommodate the write
operation. As this operation can fail, code must be written to handle that eventuality, however unlikely. Even when the probability of failure is low, it is not zero. And in this case, there
is no way to prove that it is zero. Not because a yet-to-be-discovered proof exists, but because of the nature of program resources.

Program Resources
-----------------

Programming is about managing resources. And because they are external to your program, and not within the control of your program, external changes can break your code. A file write that has always
worked may mysteriously start failing in production. You run some tests locally and everything is OK. Looking at your code you see no reason why it should fail. You run your tests in production and they fail.
You look at the disk and discover it has been marked as read-only since the last reboot.

When code fails we must keep in mind that code runs in a context, as one component of a larger system. This is one reason why comparing test outcomes from local and production environments can yield confusing
results. As a rule of thumb, if code works locally but not in some other enviornment, it is worth finding out what it is in the other *environment* that is different.

Notes
-----

catching an exception and logging it is not handling the error.

(point on engineering, if outofmemoryerror was out ofmemoryexception)

tool to test all assumptions of code, if writing to disk, tests for a number of assumptions about that disk, e.g., rw, amount space, ...
