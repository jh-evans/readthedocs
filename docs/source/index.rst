.. toctree::

   index
   using

The Darien Project
==================

The Darien Project for Java code makes handling Java code failure easy. It's well-known that error-handling code is buggy [1]. Using the library allows you to easily focus on the failure path to build better, working code more quickly.

You can ask questions at info@darien-project.org.

.. quickStart:
Quick Start
-----------

The call to ``m.getPage`` below may fail in two ways: its internal HTTP GET might return a status value outside the 200 to 299 success range, or ``getPage`` might have encountered an exception. Either
way, the failure path will be executed.

.. literalinclude:: /code/main.java
   :language: java
   :linenos:

Darien Library tool support will write the code invocation for you, the ``if``, ``else``, and ``switch``, you see below so that you can focus on what you need to.

We handle the two failure cases like this (the implementation of ``getPage`` is defined in getPage_):

.. literalinclude:: /code/main_failurevalue.java
   :language: java
   :linenos:

The ``switch`` on ``page`` above is an example of pattern matching, released in Java SE 17 (https://openjdk.org/jeps/406) \[2\].

Running the above, attempting to retrieve ``https://www.example.com/nosuchpage``, results in a 404 being returned, wrapped in a ``FailureValue``.

As below, when ``getPage`` is passed ``https://www.cannotfindthisdomain.com``, an instance of ``FailureException`` is returned.

.. literalinclude:: /code/main_failureexception.java
   :language: java
   :linenos:

All failure-describing types (``FailureValue``, ``FailureException`` and others) are subtypes of ``F`` (see The Detail below) which in turn is a subtype of ``S``. ``S``'s ``eval`` returns ``true``, whereas
``eval`` on ``F`` and its subtypes returns ``false``. Within the failure path (the else), the appropriate failure instance (``fv`` or ``fe``) is created via the type switch.
That is it. You are done.

This approach focuses on the different kinds of failure, cleanly separating all cases, and tool supports write the handling code.

.. Detail:
The Detail
----------

``S`` is a type that wraps an instance and defines two methods. ``unwrap`` returns the instance and ``eval`` returns ``true``. Generics are not used. This is explained in Generics_.

.. literalinclude:: /code/S.java
   :language: java
   :linenos:

``F`` is the root of all failure-describing types:

.. literalinclude:: /code/F.java
   :language: java
   :linenos:

All subtypes of ``F`` override ``eval`` to return ``false``.

The failure-describing types below (such as ``FailureValue``) are wrappers around an instance associated with the failure, such as a value or exception.

``FailureValue`` is defined as:

.. literalinclude:: /code/failurevalue.java
   :language: java
   :linenos:

``FailureValue`` wraps a ``Number``. This type is useful when an operation has failed and a code value associated with the failure is to be returned, as in the HTTP GET 404 above.

``FailureException`` wraps an exception in the same way:

.. literalinclude:: /code/failureexception.java
   :language: java
   :linenos:

.. getPage:
getPage
--------

When ``url`` is ``https://www.cannotfindthisdomain.com``, ``getPage`` returns a ``FailureException`` that wraps the thrown ``java.net.UnknownHostException``.
If ``url`` is ``https://www.example.com/nosuchpage``, ``getPage`` will return a ``FailureValue`` that wraps the number 404.

.. literalinclude:: /code/getpage.java
   :language: java
   :linenos:

Although ``getPage`` looks perfectly reasonable, ``url`` may be null. Or ``url`` should be rejected if it does not use SSL (https), as implemented below.

.. literalinclude:: /code/getpage-checked-url.java
   :language: java
   :linenos:

Note: ``Result`` is a ``static class`` defined in the same class as ``getPaage`` that passes the response code and the retrieved webpage from ``execute`` so it can be assigned to ``result``.

.. literalinclude:: /code/result.java
   :language: java
   :linenos:

.. Generics:
Generics
----------

Types ``S`` and ``F`` do not use generics. This means that you must explicitly cast the result of ``unwrap``. A generic ``S<T>`` would remove the need for the cast. However, in the failure case
(which has more types), you would be required to enter the generic type for the success case, e.g., ``FailureValue<String>``, which is redundant as failure types are containers for failure objects.
Primarily, generics have not been used to ensure code brevity.

Using Interfaces
----------------

You will note that ``S``, ``F``, and all the failure-describing types, are Java interfaces. You use these types when *using* the library, as a consumer, as in the ``main`` methods
in Quick Start above.

When you produce success and failure cases, you use an *implementation* class of these types, as in getPage_ (such as the class ``Success``).

As an engineer, you reason about success and failure and how to handle these cases using the types. You give these types concrete meaning at run-time by using the classes in ``org.darien.types.impl``. In
this code design, classes are purely a mechanism for expressing code and its reuse.

Focusing on Failure Leads to More Robust Code
---------------------------------------------

By focusing on failure with the above approach, we see that:

1. Any method parameter can cause your code to fail
2. All code paths are terminated at a ``return``
3. Any search code can fail

The Darien approach is to check parameter values for ``null``, returning an appropriate failure instance. The library supports you here with its calls to ``FailureUtils.oneIsNull``
and ``FailureUtils.theNull``.

For point 2., the Darien approach is to return exceptions wrapped in a ``FailureException``. This style is preferred over throwing an exception because where an exception is caught might be a long way
from where it is generated, reducing options for addressing the issue. However, adopting this style is a matter of preference.

All code that searches for something (or that looks something up or relies on something being present) can fail when the item assumed to be there is absent. To highlight this, the following
extracts the right-hand side of a string containing a hyphen of the form "lhs-rhs".

.. literalinclude:: /code/rhs.java
   :language: java
   :linenos:

If ``input`` is ``hyphen-ated``, ``rhs`` will return ``ated``. But if ``input`` is ``hyphenated``, an ``ArrayIndexOutOfBoundsException`` will be raised. This code handles that failure case:

.. literalinclude:: /code/rhs_darien_version.java
   :language: java
   :linenos:

Resources
---------

| \[1\] `The original LinkedIn article <https://www.linkedin.com/pulse/failure-subtype-success-huw-evans/>`_
| \[2\] `Baeldung.com <https://www.baeldung.com/java-switch-pattern-matching>`_, `Oracle Help Center <https://docs.oracle.com/en/java/javase/17/language/pattern-matching.html#GUID-A59EF0C7-4CB7-4555-986D-0FD804555C25>`_
