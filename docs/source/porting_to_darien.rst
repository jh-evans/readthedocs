Porting your code to the Darien Library
=======================================

It's really straightforward. Let's assume you have the following code:

.. literalinclude:: /code/original_getfile.java
   :language: java
   :linenos:

This is a typical Java method that takes a parameter, returns a result and may throw an exception.

As we know, it is possible for parameters to be null. In this case, even if not null, ``filename`` may indicate a file that does not exist or is inaccessible.

On looking at the code, it appears that if ``filename`` refers to a file that does not exist, a ``FileNotFoundException`` would be thrown. However, in this case, that can never happen. This is because
``FileNotFoundException`` is a subclass of ``IOException`` and so a ``FileNotFoundException`` will be handled at the catch for ``IOException``. If this occurs, the code prints the stack trace and returns an
empty string as the string builder has not had any data appended to it. This may or may not be the intended behaviour. But it is worse; the method's behaviour is ambiguous. We cannot tell the difference between a file that does not exist and
one that does exist, which is zero length, other than watching for a stacktrace on standard output if it is available. One solution is to explicitly catch the ``FileNotFoundException`` and re-throw it so
that the calling code is made aware that the file cannot be found.

Moving your code to the Darien Library
--------------------------------------

There are four areas that might cause your code to fail:

1. Method parameters that are null
2. Method parameters that fail a test that would cause the rest of your code to fail
3. The exceptions and errors that might be raised
4. What is returned from the method

Looking at the above code:

1. ``filename`` may be null
2. The filename may refer to a file that does not exist
3. An ``IOException`` that is not a file not found issue may occur, e.g., in between the file being successfully opened at the ``try`` line and the contents being read with ``readLine``, the file may become unavailable
4. The code either returns an empty string or the contents of the file

Re-writing the code
-------------------

.. literalinclude:: /code/original_getfile_rewrite.java
   :language: java
   :linenos:

1. Line two prevents filename from being ``null``
2. Line six tests whether the file exists
3. Line 18 wraps an ``IOException`` into a returned value. If an empty string is returned at line 21, the caller can be confident the file was zero length, removing the ambiguity above
4. Line 21 wraps the file contents

An additional case shoudl be considerede. It is possible that some of a file has been read into ``resultStringBuilder`` before an ``IOException`` is raised. If this is the case, the rewrite ignores
the partially read file, returning the failed exception object at line 18. If this partially read file should be passed back, an instance of the type ``FailurePartialResult`` can be returned at line 18.

public S getFile(Path filename) {
    if(FailureUtils.oneIsNull(filename)) {
        return FailureUtils.theNull(filename);
    }

    File file = filename.toFile();
    if(!file.exists()) {
        return FailureUtils.theFalse(file.exists());
    }

    String line;
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
        while ((line = br.readLine()) != null) {
            resultStringBuilder.append(line).append("\n");
        }
    } catch (IOException e) {
        return new FExp(e);
    }

    return new Success(resultStringBuilder.toString());
}
