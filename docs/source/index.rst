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

All failure-describing types (``FailureValue``, ``FailureException`` and others) are subtypes of ``Failure`` (see `The Detail <theDetail>`_ below), a subtype of ``Success``. ``Success`` defines ``eval`` which returns ``true``. ``eval`` on ``Failure`` and its subtypes return ``false``. Within the failure path (the else), the appropriate failure instance (``fv`` or ``fe``) is created via the type switch. That is it. Easy.

This approach focuses on the different kinds of failure, cleanly separating the various cases, without over-focusing on success and forgetting to deal with failure.

.. theDetail:
The Detail
----------

``Success`` is a type that wraps an instance of ``T``. ``unwrap`` returns the instance. ``eval`` returns ``true`` so your code will travel down the success path.

.. code-block:: java
   :linenos:

   public interface Success<T> {	
       public boolean eval();
       public T unwrap();
   }

``Failure`` is the root of all failure-describing classes:

.. code-block:: java
  :linenos:

   public interface Failure<T> extends Success<T> {
   }

All subtypes of ``Failure`` override ``eval`` to return ``false`` so your failure handling code passes through the ``else`` above.

``Failure`` extends ``Success`` for the same type ``T`` so that ``Failure`` subtypes can be passed back wherever an instance of ``Success`` is expected (`see ``getPage`` <getPage>`_).

The failure-describing types below (such as ``FailureValue``) are wrappers around an instance associated with the failure, such as a value or exception. This is because in the failure case, the instance
of type T is not used as T is associated with a successful operation. However, for Java type correctness, ``Failure`` must be typed from ``T``. 

``FailureValue`` is defined as:

.. code-block:: java
   :linenos:

   public interface FailureValue<T> extends Failure<T> {
       public Number getValue();
   }

``FailureValue`` wraps a ``Number`` which is useful when an operation has failed and a code value is to be associated with that failure, as in the HTTP GET 404 above.

``FailureException`` wraps an exception:

.. code-block:: java
   :linenos:

   public interface FailureException<T> extends Failure<T> {
       public Exception getException();
   }

.. getPage:
`getPage`
--------

When ``url`` is ``https://www.cannotfindthisdomain.com``, ``getPage`` will return a ``FailureException`` that will wrap the thrown ``java.net.UnknownHostException``.
When ``url`` is ``https://www.example.com/nosuchpage``, ``getPage`` will return a ``FailureValue`` that will wrap the number 404.

You can update ``getPage`` to more explicitly handle the other error cases when ``url`` is malformed or null.

.. code-block:: java
   :linenos:

   public Success<String> getPage(String url) {
       try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
           final HttpGet httpget = new HttpGet(url);
   
           Result result = httpclient.execute(httpget, response -> {
               return new Result(response.getCode(), EntityUtils.toString(response.getEntity()));
           });
   
           if(result.status_code >= 200 && result.status_code <= 299) {
                   return new SuccessImpl<String>(result.page);
           } else {
                   return new FailureValueImpl<String>(result.status_code);
           }
       } catch(java.io.IOException ioe) {
               return new FailureExceptionImpl<String>(ioe);
       } catch(Exception e) {
               return new FailureExceptionImpl<String>(e);
       }
   }

``Result`` is a ``static class`` defined in the same class as ``getPaage`` used to pass the response code and the retrieved webpage from ``execute`` so it can be assigned to ``result``.

.. code-block:: java
   :linenos:

   private static class Result {
       public final int status_code;
       public final String page;

       public Result(int i, String str) {
           this.status_code = i;
           this.page = str;
       }
   }

Using Interfaces
----------------

You will note that ``Success``, ``Failure``, and all the failure-describing types, are Java interfaces. You use these types when _using_ the failure library, as a consumer, as in the ``main`` methods
in QuickStart_.

When you write your code to make use of the failure library (as a producer of success and failure) you use an implementation of these types as you can see in `getPage <getPage>`_ (e.g., ``SuccessImpl``).

As an engineer, you reason about success and failure using the types and implement those types to give them concrete meaning at run-time. In this design, classes are purely a mechanism for
expressing code and its reuse.

Focusing on Failure Leads to More Robust Code
---------------------------------------------

By focusing on failure, we can see that:

1. Any method parameter can cause your code to fail
2. Any code that searches for something can fail

One way to handle point 1. is to use pre-conditions and appropriately return a failure instance.

Code that searches for an item is quite common. The following extracts the right-hand side of a string containing a hyphen of the form "lhs-rhs".

.. code-block:: java
  :linenos:

   private String rhs(String input) {
       return input.split("-")[1];
   }

If ``input`` is ``hyphen-ated``, ``rhs`` will return ``ated``. But if ``input`` is ``hyphenated``, an ``ArrayIndexOutOfBoundsException`` will be raised. This addresses that problem:

.. code-block:: java
   :linenos:

   private Success<String> rhs(String input) {
       try {
           return new SuccessImpl<String>(input.split("-")[1]);
       } catch(ArrayIndexOutOfBoundsException oobe) {
           return new FailureExceptionImpl<String>(oobe);
       }
   }

The above code is an improvement but it doesn't handle all error cases, e.g., ``input`` might be ``null``.

Resources
---------

| \[1\] `The original LinkedIn article <https://www.linkedin.com/pulse/failure-subtype-success-huw-evans/>`_
| \[2\] `Baeldung.com <https://www.baeldung.com/java-switch-pattern-matching>`_, `Oracle Help Center <https://docs.oracle.com/en/java/javase/17/language/pattern-matching.html#GUID-A59EF0C7-4CB7-4555-986D-0FD804555C25>`_

This is the Slate Githubs Pages Theme
-------------------------------------

See `the README.md file at the pages-theme repository <https://github.com/pages-themes/slate/>`_

To use the Slate theme:

1. Add the following to your site's ``_config.yml``:

    .. code-block:: yml
       remote_theme: pages-themes/slate@v0.2.0
       plugins:
       - jekyll-remote-theme # add this line to the plugins list if you already have one

2. Optionally, if you'd like to preview your site on your computer, add the following to your site's `Gemfile`:

    .. code-block:: ruby
       gem "github-pages", group: :jekyll_plugins
