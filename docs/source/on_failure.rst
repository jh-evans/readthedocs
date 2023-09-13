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

Programming is about managing resources. And because these resources are external to your program, and not within its control, external changes can break your code. A file write that has always
worked may mysteriously start failing in production. You run some tests locally and everything is OK. Looking at your code you see no reason why it should fail. You run your tests in production and they
fail. Later, you discover the disk was marked as read-only during the last reboot.

When programs fails we must keep in mind that code runs in a context, as one component of a larger system. This is one reason why comparing test outcomes from local and production environments can yield
aparently confusing results. As a rule of thumb, if code works locally but not in some other enviornment, it is worth finding out what it is in the other *environment* that is different and how that
external difference is affecting your code.

Responding to Failure
---------------------

When you call a function or method, that code is either going to give you the result you want or something else is going to happen. Failure comes in three types: normal, no-progress, process-wide.

An example of a normal failure is when looking for a substring in a longer piece of text. If you find it, the longer string conforms to what you need. Without the substring, you consider the
longer text invalid and you can easily write code to reject it.  Importantly, this failure is a normal part of your code.

No-progress errors stop your code from making meaningful progress. An example is calling a remote service. If such a sevice cannot be reached (because there is no network access), your code cannot
make a decision on what is to be done next. Such failure prevents further progress. All that can be done is to log the service access outage but your process keeps correctly executing and will, possibly, be
able to access the service in future.

A process-wide failure is more serious. Your process attempts an operation and a failure occurs that causes it to stop running, such as an uncaught exception or running out of memory. Nothing can be done but
for the process to log the error (if possible) and to terminate.

The lines between failure types are not always clear. Without access to the remote service, your program may correctly execute, but is unable to achieve anything useful until the service is back.  Until
that happens, your program has effectively failed process-wide. It is not providing any value and may as well not be running.

Where in code to Respond to Failure
-----------------------------------

There are three locations that can respond to a non-successful outcome:

1. The called code, for example, the code that invokes a remote service
2. The calling code that makes use of the called code
3. Some other piece of code

Code you call may fail in one of the three ways described above: normal, no-progress or process-wide. If it is process-wide, your program stops as the error is serious enough to prevent it from running
any more. If failure is normal, the called code will respond in a way that can be handled by the calling code in the normal course of operation. No-progress errors are likely to require reporting elsewhere,
for example, to inform a user, or your calling code can attempt to recover the situation, possibly by reporting the issue to a third-party process whose role is to rectify the situation.

For the happy path, the called code is a black box. You request the code to do something, it succeeds and you get an appropriate result.

However, when considering the failure path, the called code is not a black box. The called code has failed, reporting information that indicates detail within the black box. This detail is usually
required to enable the calling code to appropriately respond to the error. The detail may indicate that a remote system is not responding or that a disk write failed.

The called code is communicating, "I have failed because I cannot handle this case, and you need to know about it".

There is a fundamental difference between code being made aware of an error and that code being able to successfully resolve an error experienced by some other piece of code.

In the current code path, if the calling code cannot address the issue, all that can be done is to log the error and continue processing.

Other code may need to be made aware of the failure. Ideally, code failure is constrained to a small part of the program. If this is not possible, the calling code is returned to some other part of the
application, the thinking being that some other piece of code can more appropriately respond to the issue.

Importantly, noting that an error has occurred is not the same thing as rectifying the situation so that the error will not happen next time. All we are doing is recording that an error happened. But that is
all that can be done at times.

This is because the failure occurs outside your program, within an external resource.

If a remote system has failed, the number of reasons for its failure is significant, and its cause will be well beyond the calling code's ability to address. Your code is designed to solve some other
problem, not rectifying issues in the infrastructure required to enable your code to run. Therefore, when a failure does arise, it is very common that the only response is to log the issue, and move on.

Notes
-----

catching an exception and logging it is not handling the error.

(point on engineering, if outofmemoryerror was out ofmemoryexception)

tool to test all assumptions of code, if writing to disk, tests for a number of assumptions about that disk, e.g., rw, amount space, ... 
