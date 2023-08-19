Porting your code to the Darien Library
=======================================

It's really straightforward. Let's assume you have the following code:

.. literalinclude:: /code/original_getfile.java
   :language: java
   :linenos:

This is a typical Java method that takes a parameter, returns a result, and may throw an exception.

As we know, parameters can be null. In this case, even if not null, ``filename`` may indicate a file that does not exist or is inaccessible, leading to some kind of ``Throwable``.

Looking at the method signature, it appears that if ``filename`` refers to a non-existent file, a ``FileNotFoundException`` would be thrown out to the calling code. However, in this case, that can never
happen because  ``FileNotFoundException`` is a subclass of ``IOException`` and the ``catch`` will handle it. If this occurs, the code prints the stack trace and returns an empty string as the string builder
has not had any data appended to it. This may or may not be the intended behaviour. But it gets worse.

The method's behaviour is ambiguous. We cannot tell the difference between a file that does not exist and one that does exist but is zero length. One solution is to explicitly catch the
``FileNotFoundException`` and re-throw it so that the calling code is made aware that the file cannot be found.

Moving your code to the Darien Library
--------------------------------------

There are four areas to consider:

1. Method parameters that are null
2. Method parameters that fail a test that could cause your code to fail
3. Exceptions or errors that are raised
4. What the method returns

Looking at the above code:

1. ``filename`` may be null
2. A valid filename may refer to a file that does not exist
3. An ``IOException`` that is not a file not found issue may occur, e.g., in between the file being successfully opened at the ``try`` on line 5 above and the contents being read with ``readLine``, the file may become unavailable
4. The code either returns an empty string or the contents of the file

For point 3., it is ``readLine`` that might throw an ``IOException`` and the number of ways an ``IOException`` might be raised is varied. For example, the drive the file resides on --- such as a USB thumb drive --- could be removed from the system. Or an external process deletes the file.

Re-writing the code
-------------------

.. literalinclude:: /code/original_getfile_rewrite.java
   :language: java
   :linenos:

1. Line two prevents filename from being ``null``
2. Line seven tests whether the file exists
3. Line 18 wraps an ``IOException`` into a returned value. If an empty string is returned at line 21, the caller can be confident the file was zero length, removing our ambiguity
4. Line 21 wraps the file contents

For point 2., if the file exists at line 7 but does not at line 13, an ``IOException`` will be wrapped and returned inside a ``FailureException`` type.

An additional case should be considered. Some of a file could have been read into ``resultStringBuilder`` when an ``IOException`` is raised. If this is the case, the rewrite ignores
the partially read file by returning the failed exception object at line 18. If this partially read file should be passed back, an instance of the type ``FailurePartialResult`` can be used.
