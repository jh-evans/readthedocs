Using the Darien Library
========================

It is a truth seldom acknowledged that developers mostly focus on the `Happy Path <https://en.wikipedia.org/wiki/Happy_path>`_. Developers are busy with lots to do. And today your challenge is to 
retrieve a field from a Java object, even when that field is marked private, so you develop ``getField`` below.

You then write a couple of unit tests to be sure that what you have written retrieves a named field. For each test, you pass in the required name of the field, its classname, and the object the 
field is to be retrieved from.

Your tests work, and you deploy your code.

.. code-block:: java
   :linenos:
   :caption: Your initial implementation of ``getField``
   :emphasize-lines: 3-6

   public static Object getField(String classname, String fieldname, Object inst) {
       	try {
       		Class<?> cls = Class.forName(classname);
       		Field fld = cls.getDeclaredField(fieldname);
       		fld.setAccessible(true);
       		return fld.get(inst);
       	} catch (ExceptionInInitializerError eiie) {
       		log(eiie);
       	} catch (ClassNotFoundException cnfe) {
       		log(cnfe);
       	} catch (NoSuchFieldException nsfe) {
       		log(nsfe);
   		} catch (SecurityException se) {
       		log(se);
   		} catch (IllegalArgumentException ile) {
       		log(ile);
   		} catch (NullPointerException npe) {
       		log(npe);
   		} catch (IllegalAccessException iae) {
       		log(iae);
   		}
       	
       	return null;
   }

However, there are a number of points to note:

1. What if one of the method parameters is null?
2. The method returns null if an exception is thrown, potentially propagating null around the codebase
3. Your two unit tests only test the happy path

A null method parameter will result in a ClassNotFoundException, NoSuchFieldException or NullPointerException being thrown, logged and the
method returning null. Passing back null requires the code that calls ``getField`` to distinguish these two cases or else another
NullPointerException will be thrown, which might remain a silent bug in the code.

As each of the three method parameters could be null and any of the seven ``catch`` statements might occur, there are ten different ways that this
method might fail. The happy path represents the code block in the ``try`` statement (highlighted) running to completion with no issues.

You decide to rewrite the above using the Darien Library.

The Darien Library
==================

The rewrite wraps your results in Darian Libray objects and tool support generates the code to unwrap them.

The code above becomes:

.. code-block:: java
   :linenos:
   :emphasize-lines: 1, 2-4, 10

   public static S getField(String cn, String fn, Object inst) {
            if(FailureUtils.oneIsNull(cn, fn, inst)) {
              	return FailureUtils.theNull(cn, fn, inst);
            }
      
          	try {
          		Class<?> cls = Class.forName(cn);
          		Field fld = cls.getDeclaredField(fn);
          		fld.setAccessible(true);
          		return new Success(fld.get(inst));
          	} catch (ExceptionInInitializerError eiie) {
              	return new FErr(eiie);
          	} catch(ClassNotFoundException cnfe) {
          		return new FExp(cnfe);
          	} catch (NoSuchFieldException nsfe) {
          		return new FExp(nsfe);
      		} catch (SecurityException se) {
          		return new FExp(se);
      		} catch (IllegalArgumentException ile) {
          		return new FExp(ile);
      		} catch (NullPointerException npe) {
          		return new FExp(npe);
      		} catch (IllegalAccessException iae) {
          		return new FExp(iae);
      		}
   }

``getField`` returns an instance of the type ``S``. All of the method parameters are passed to ``FailureUtils.oneIsNull``, which returns ``true`` if one of them is null. ``FailureUtils.theNull`` returns
an instance of the type ``FailureArgIsNull`` that lists the arguments that are null along with the filename and line where this instance was created. This is useful when tracing issues in
deployed, live systems.

Line 10 returns the retrieved field, wrapped in a ``Success`` class that implements the ``S`` type.

The ``ExceptionInInitializerError`` and all of the exceptions are caught and returned wrapped in an appropriate ``Failure`` type, ``Ferr`` or ``FExp``.

.. Considering the failure cases helps you write better tests.

Calling ``getField``
--------------------

The invocation of the rewritten ``getField`` is:

.. code-block:: java
   :linenos:

   FailureArgIsFalse faif = FailureUtils.theFalse(new Boolean[] {false, false});    	
   S obj = TestUtils.getField("org.darien.types.impl.ArgsList", "idxs", faif);
    	
   if(obj.eval()) {
     List<Number> idxs = (List<Number>) obj.unwrap();

     assertTrue(idxs.size() == 2);
     assertTrue((int)idxs.get(0) == 0);
     assertTrue((int)idxs.get(1) == 1);
    } else {
      switch (obj) {
        case FailureError err -> assertTrue(err.getLocation(), false);
        case FailureException exp -> assertTrue(exp.getLocation(), false);
        case FailureArgIsNull fain -> assertTrue(fain.getLocation(), false);
        default -> assertTrue(false);
      }
    }

The above code is taken from a unit test and you do not need to write it, Darien tool support writes it for you.

``getField`` (line 2) is called with a classname, fieldname and instance.

An object (``obj``) of type ``S`` is returned. If ``eval`` returns true, ``obj`` represents the success case and ``unwrap`` is called. Otherwise, the call has failed and the ``switch`` on  line 11
is executed.

In the success case, ``unwrap`` returns the result from line 10 of the implementation of ``getField`` above (``fld.get(inst)``).

If the failure path is execued, the ``switch`` on ``obj`` executes and ``obj`` is cast into one of the three failure types generated from the eight ways the method can fail (``FailureError``,
``FailureException```, and ``FailureArgIsNull``). In each case, an assertion fails (on the righthand side of the ->), passing in a string message from ``getLocation`` that describes where in the
code the failure type was created.

As written, the default case cannot execute as ``obj`` will only be one of the three failure types. If ``getField`` returned an additional type, the switch would have to be updated with an explicit
case or else the default would exceute. This is the reason for the assertion failure on the default line.

Advantages of this Approach
---------------------------

The advantages of this approach are:

1. The failure and success paths are now explicit
2. The different ways that ``getField`` can fail has been captured in code
3. No ``null`` value has been returned from ``gettField``
4. The code to handle the two path is standard and easy to follow
5. Darien tools generate the code above so that you can focus on what you need to do
6. Considering the failure cases helps you write better tests.
