Using
=====

It is a truth seldom acknowledged that developers mostly focus on the `Happy Path <https://en.wikipedia.org/wiki/Happy_path>`_. Developers are busy, and they have lots to do. You need to write some code
to retrieve a field from a Java object, even when that field is marked private, so you write this to meet your requirement.

You then write some unit tests to be sure that what you have written retrieves a private field. For each test, you pass in the required classname, fieldname, and instance the field is to be retrieved from.

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
1. What if any of the method parameters are null?
2. The method returns null if an exception is thrown
3. Your tests only test the happy path

Considering the non happy path, help generate better tests.
