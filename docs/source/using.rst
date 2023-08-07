Using
=====

This is the main type when using the success path in the Darien project. You use it like this:

.. code-block:: java
   :linenos:

   // This code is taken from the Darien Project Unit Tests
   //
   // getField is the code that you are calling that may fail
   // It returns objects that are typed from the interfaces in this package
   
   FailureArgIsFalse faif = FailureUtils.theFalse(new Boolean[] {false, false});
   S obj = TestUtils.getField("org.darien.types.impl.ArgsList", "idxs", faif);
   
   if(obj.eval()) {
       // This is the success path. The code above has returned a valid result
       // We unwrap the result from the above code and your code then uses this object as if it had
       // been passed back from the code called above (getField in our case)
       List<Number> idxs = (List<Number>) obj.unwrap();
   
       assertTrue(idxs.size() == 2);
       assertTrue((int)idxs.get(0) == 0);
       assertTrue((int)idxs.get(1) == 1);
   } else {
        // This is the failure path. In the failure case, getField will have returned an object
        // that is a subtype of F (which is a subtype of S). In our case, getField returns objects
        // of type FailureError and FailureException (that wrap Java error objects and exceptions, respectively)
   
        // This switch on the object 'obj' is a switch on its type
        // If a FailureError has been passed back, obj is cast to FailureError, now called err
        // and the code on the right-hand side of the -> can now use err. The same for FailureException
        //
        // When dealing with failure in a production system (lots of services, lots of logs) every object
        // of type F (so FailureError and FailureException) define getLocation. This is a string of this
        // form that tells you where the failure occurred in your code:
        //   org.darien.types.utils.tests.TestUtils.getField(TestUtils.java:21)
        // In this case, the location string is output as part of a unit test failure
        //
        // Note, as currently written, getField above only returns types S, FailureError and FailureException
        // Therefore, the 'default' case cannot currently occur. However, if getField was updated to return a
        // third failure type, the default case would be used.
   
      switch (obj) {
          case FailureError err -> assertTrue(err.getLocation(), false);
          case FailureException exp -> assertTrue(exp.getLocation(), false);
          case FailureArgIsNull fain -> assertTrue(fain.getLocation(), false);
          default  -> System.out.println("As currently written, not possible.");
      }

This is no longer the code block.
