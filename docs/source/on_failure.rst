On Failure
==========

Programs + Resources = Failure. All resources are external to your program, even the memory, and an attempt to access a resource may fail. Writing to a file can fail as the disk might be full. Reading a
file might error as its permissions have been made too restrictive. Creating a list may fail as there is not enough memory.

The likelihood of some errors is low enough that, for all practical purposes, they are treated as if they will not occur. In a programming language, when allocating the next value, it is possible to run out of
memory. Do you write code to explicitly handle this out-of-memory case? No, of course not; no one does that.

When considering code failure, it is useful to distinguish between errors that cannot occur from those that will not occur. The former can be proved; the latter is an opinion.

In a statically typed programming language, a value of type integer can only be used as an integer and any attempt to do differently results in a compilation error. Therefore, errors that might otherwise
arise because of an attempt to use the integer as if it were another type *cannot* occur. The compiler prevents all attempts.

When you write code to put a file onto disk, you are likely to make sure there is enough space to accommodate it. Your goal is to write code to successfully persist data. What do you need to help you
achieve that goal? Enough disk space. However, available space is an assumption that eventually will be false. The disk where you are writing the data can, at any point, be too full to accommodate the write
operation. As this operation can fail, code must be written to handle that eventuality, however unlikely. Even when the probability of failure is low, it is not zero. And in this case, there
is no way to prove that it is zero. Not because a yet-to-be-discovered proof exists but because of the nature of program resources.

Program Resources
-----------------

Programming is about managing resources. And because these resources are external to your program and not within its control, external changes can break your code. A file write that has always
worked may mysteriously start failing in production. You run some tests locally, and everything is OK. Looking at your code, you see no reason why it should fail. You run your tests in production, and they
fail. Later, you discover the disk was marked as read-only during the last reboot.

When programs fail, we must keep in mind that code runs in a context as one component of a larger system. This is one reason why comparing test outcomes from local and production environments can yield
apparently confusing results. As a rule of thumb, if code works locally but not in some other environment, it is worth finding out what it is in the other *environment* that is different and how that
external difference is affecting your code.

Responding to Failure
---------------------

When you call a function or method, that code is either going to give you the result you want or something else is going to happen. Failure comes in three types: normal, no-progress, process-wide.

An example of a normal failure is when looking for a substring in a longer piece of text. If you find it, the longer string conforms to what you need. Without the substring, you consider the
longer text invalid, and you can easily write code to reject it.  Importantly, this failure is a normal part of your code.

No-progress errors stop your code from making meaningful progress. An example is calling a remote service. If such a service cannot be reached (because there is no network access), your code cannot
make a decision on what to do next. Such failure prevents further progress. All that can be done is to log the service access outage, but your process keeps correctly executing and will possibly be
able to access the service in future.

A process-wide failure is more serious. A failure occurs that causes your whole process to stop running, such as running out of memory. Nothing can be done but for the process to log the issue (if possible) and to terminate.

The lines between failure types are not always clear. Without access to the remote service, your program may correctly execute but is unable to achieve anything useful until the service is back.  Until
that happens, your program has effectively failed process-wide. It is not providing any value and may as well not be running.

<in general, failures that are not process-wide should all be tolerated such that your code can make useful progress>. Expand on this.

Where in code to Respond to Failure
-----------------------------------

There are three locations that can respond to a non-successful outcome:

1. The called code, for example, that invokes a remote service
2. The calling code that makes use of the called code by invoking it
3. Some other piece of code

Code you call may fail in one of the three ways described above: process-wide, normal, no-progress. If it is process-wide, your program stops as the error is serious enough to prevent your entire program
from running. If failure is normal, the called code will respond in a way that can be handled by the calling code in the normal course of events.

No-progress errors mean your code cannot make meaningful progress. This situation usually requires reporting to make sure someone is aware of the situation so that it can be rectified. The user may also be
informed as a lack of progress may affect them.

For the happy path, the called code is a black box. You request the code to do something, it succeeds, and you get an appropriate result.

However, when considering failure, the called code is not a black box. Failure reports reveal information within the black box. This is usually required so that the calling code can respond to the
error. The detail may show that a remote system is not responding or that a disk write failed. This reveals information on how the black box works.

The code is communicating, "I have failed because I cannot handle this case, and you need to know about it."

However, there is a fundamental difference between code being made aware of an error and the code being able to successfully resolve it. The former is easy; the latter is hugely non-trivial.

It may make sense that knowledge of the failure is passed on. The calling code returns the failure detail to some other part of the application, the thinking being that code elsewhere can more
appropriately respond to the issue.

Importantly, noting that an error has occurred is not the same thing as rectifying the situation so that the error will not happen next time. Noting an error records that it happened. But, in the
complex systems of today that is usually all that can be done.

This is because the failure occurs outside your program, in an external resource.

External systems may fail for any number of reasons. If a call to write to a disk fails, how to address that issue is well beyond the capabilities of the calling code. Your code is designed to solve
some other problem and is not designed to be involved in rectifying issues in the infrastructure required to enable your code to run. Therefore, in such a system, when a failure does arise, it is
reasonable that the only response is to log the issue and move on. In this way, the code should be able to tolerate the issue but not get involved in error repair. This is because to repair the error in
this case, your code would, first of all, have to know how to diagnose the issue (the disk might be full, it might be marked read-only, or it might be one of a hundred other things), and once successfully
diagnosed, have a working remedy put into place, such that, a subsequent write would then work. Both of these are non-trivial.

Some errors are transitory, and here, the calling code can tolerate such issues so that useful progress can be made. For example, if your code is running on a mobile device that is currently out of network
range, any attempt to contact a remote service will fail. However, the device may move within range at any time, so re-trying the call *may* make sense in such circumstances. A detailed appreciation of the
likely cause of failure in such a system (a failure model) is needed so that the code can be designed to react well to the realities it will face. Note, in this case, the code running on the mobile device is
just re-trying the call in the hope that the underlying cause was only temporary. The code is not making an attempt to ensure a signal is available - that is not possible.

What we see is that a program's ability to tolerate failure depends on the nature of that failure.

Notes
-----

(point on engineering, if outofmemoryerror was out ofmemoryexception)

tool to test all assumptions of code, if writing to disk, tests for a number of assumptions about that disk, e.g., rw, amount space, ... 
