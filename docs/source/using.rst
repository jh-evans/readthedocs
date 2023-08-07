Using the Darien Library
========================

It is a truth seldom acknowledged that developers mostly focus on the `Happy Path <https://en.wikipedia.org/wiki/Happy_path>`_. Developers are busy with lots to do. Today, you need to write some code to retrieve a field from a Java object, even when that field is marked private, so you
type this to meet your requirement.

You then write some a couple of unit tests to be sure that what you have written retrieves a named field. For each test, you pass in the required classname, fieldname, and object the field is to be retrieved from.

Your tests work, and you deploy your code.

.. code-block:: java
   :linenos:
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
2. The method returns null if an exception is thrown
3. Your two tests only test the happy path

A null method parameter will result in a ClassNotFoundException, NoSuchFieldException or NullPointerException being thrown, logged and the
method returning null. Passing back null requires the code that calls ``getField`` to distinguish these two cases or else another
NullPointerException will be thrown, which might remain as a silent bug in the code.

As each of the method parameters might be null and any of the seven ``catch`` statements could happen, there are ten different ways that this
method might fail. The happy path represents the code block in the ``try`` statement (highlightted) running to completion.

You decide to rewrite the above using the Darien Library.

The Darien Library
==================

The rewrite of the above is:

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
an instance of the type ``FailureArgIsNull`` that describes which of the arguments is null as a string along with the filename and line where this instance was created. This is useful when tracing issues in deployed, live systems.

Line 10 returns the retrieved field, wrapped in a ``Success`` class that implements the ``S`` type.

The ``ExceptionInInitializerError`` and all of the exceptions are caught and returned wrapped in an appropriate failure type, ``Ferr`` or ``FExp``.

.. Considering the failure cases helps you write better tests.
