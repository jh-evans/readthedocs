On Failure
==========

In the same way that Algorithms + Data Structures = Programs, Programs + Resources = Failure. All resources are external to your program, even the memory, and an attempt to access a resource may fail. Writing
to a file can fail as the disk might be full. Reading a file might error as its permissions have been made too restrictive. And creating a list may fail as there is not enough memory.

The likelihood of some errors is low enough that for all practical purposes they are treated as if they will not occur. In a programming language, when allocating the next value, it is possible to run out of
memory. Do you write code to explicitly handle this out-of-memory case? No, of course not, no one does that.

When considering code failure, it is useful to distinguish between errors that cannot occur from those that will not occur. The former can be proved, the latter is an opinion.

In a statically typed programming language, a value of type integer can only be used as an integer and any attempt to do differently results in a compilation error. Therefore, errors that might otherwise
arise because of an attempt to use the integer as if it was another type *cannot* occur. The compiler prevents all attempts.

When you write code to put a file onto disk, you are likely to make sure that there is enough space to accommodate the file write. Your goal is to write code to successfully write data to disk. What do you 
need to help you achieve that goal? Enough disk space. However, available space is an assumption that eventually will be false. The disk where you are writing the data can, at any point, be too full to
accommodate the write operation. When this happens, your code will fail. As it is possible for the write to fail, code must be written to handle that eventuality, however unlikely. Even when the probability
of failure is low, it is not zero. And in this case there is no way to prove that it is zero.

Notes
-----

catching an exception and logging it is not handling the error.

(point on engineering, if outofmemoryerror was out ofmemoryexception)
