The Darien Project
==================

The failure library makes handling Java code failure easy. It's well-known that error-handling code is buggy [1]. By making handling errors more convenient, you can focus on both code paths to build better, working code more quickly.

`The failure library is available here <https://github.com/jh-evans/failure-a>`_ This should be a maven reference, not to the source code.

.. quickStart:
Quickstart
----------

The call to ``m.getPage`` below may fail in two ways: its internal HTTP GET might return a status value outside the 200 to 299 success range, or ``getPage`` might encounter an exception. Either way, the failure path will be executed.

.. code-block:: java
   :caption: a caption
   :linenos:
   :emphasize-lines: 1
   :name: code-ref-name

   public static void main(String[] args) {
       Main m = new Main();
       Success<String> page = m.getPage("https://www.example.com"); // HTTP GET a webpage as a String

       if(page.eval()) {
           System.out.println("The success path");
       } else {
           System.out.println("The failure path");
       }
   }

This is more text
