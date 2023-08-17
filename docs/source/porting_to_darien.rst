Porting your code to the Darien Library
=======================================

It's really straightforward. Let's assume you have the following code:

.. literalinclude:: /code/original_getfile.java
   :language: java
   :linenos:

This is a typical Java method that takes a parameter, returns a result and may throw an exception.

As we know, it is possible for parameters to be null. In this case, even if not null, ``filename`` may indicate a file that does not exist.

On looking at the code, it appears that if ``filename`` refers to a file that does not exist, a ``FileNotFoundException`` would be thrown. However, in this case, that can never happen. This is because
``FileNotFoundException`` is a subclass of ``IOException`` and so ``FileNotFoundException`` will be caught at the catch for ``IOException``. If this occurs, the code prints the stack trace and returns an
empty string as the string builder has not had any data appended to it. This may or may not be the intended behaviour. But it is worse. We cannot tell the difference between a file that does not exist and
one that does exist, which is zero length, other than watching for a stacktrace on standard output if it is available.
