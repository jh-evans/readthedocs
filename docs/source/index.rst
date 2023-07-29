The Darien Project
==================

The failure library makes handling Java code failure easy. It's well-known that error-handling code is buggy [1]. By making handling errors more convenient, you can focus on both code paths to build better, working code more quickly.

`The failure library is available here <https://github.com/jh-evans/failure-a>`_ This should be a maven reference, not to the source code.

.. quickStart:
Quickstart
----------

The call to ``m.getPage`` below may fail in two ways: its internal HTTP GET might return a status value outside the 200 to 299 success range, or ``getPage`` might encounter an exception. Either way, the failure path will be executed.

.. code-block:: java
   :linenos:

   public static void main(String[] args) {
       Main m = new Main();

       // When called like this, GETs a webpage as a String
       Success<String> page = m.getPage("https://www.example.com");

       if(page.eval()) {
           System.out.println("The success path");
       } else {
           System.out.println("The failure path");
       }
   }

The two failure cases are handled like this (the implementation of ``getPage`` `is defined below <getPage>`_):

.. code-block:: java
   :linenos:

   public static void main(String[] argv) {
       Main m = new Main();

       // When called like this, getPage returns a FailureValue, wrapping 404
       Success<String> page = m.getPage("https://www.example.com/nosuchpage");
   
       if(page.eval()) {
           System.out.println("Success");
       } else {
           switch (page) {
               case FailureValue<String> fv -> System.out.println(fv.getValue());
               case FailureException<String> fe -> System.out.println(fe.getException());
               default  -> System.out.println("As currently written, not possible.");
           }
       }
   }

The ``switch`` on ``page`` above is an example of pattern matching, released in Java SE 17 (https://openjdk.org/jeps/406) \[2\].

Attempting to retrieve ``https://www.example.com/nosuchpage`` will result in a 404 being returned, passed back wrapped in a ``FailureValue``.

When passed ``https://www.cannotfindthisdomain.com``, ``getPage`` returns an instance of ``FailureException``.

.. code-block:: java
   :linenos:
   public static void main(String[] argv) {
       Main m = new Main();

      // When called like this, getPage returns an instance of FailureException
       Success<String> page = m.getPage("https://www.cannotfindthisdomain.com");
   
       if(page.eval()) {
           System.out.println("Success");
       } else {
           switch (page) {
               case FailureValue<String> fv -> System.out.println(fv.getValue());
               case FailureException<String> fe -> System.out.println(fe.getException());
               default  -> System.out.println("As currently written, not possible.");
           }
       }
   }

All failure-describing types (``FailureValue``, ``FailureException`` and others) are subtypes of ``Failure`` (see `The Detail`<theDetail>`_ below), a subtype of ``Success``. ``Success`` defines ``eval`` which returns ``true``. ``eval`` on ``Failure`` and its subtypes return ``false``. Within the failure path (the else), the appropriate failure instance (``fv`` or ``fe``) is created via the type switch. That is it. Easy.

This approach focuses on the different kinds of failure, cleanly separating the various cases, without over-focusing on success and forgetting to deal with failure.

.. theDetail:
The Detail
----------
