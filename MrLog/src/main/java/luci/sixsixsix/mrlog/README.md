# Mr. Log
### Advanced logger for Android
The package contains 2 implementations for the MrLogI Interface in case the logger is injected.
- **MrLog** The main logger object.
- **MrLogNoLogs** Will print no logs.

Mr.Log will log any object passed, even if it's null.
You can pass as many objects as you want.

**Example usage:**
Mr.Log will print `Class.method(lineNumber)	elem1 **** elem2`
```
data class TestCl (
	val a: String = "some string",
	val b: Float = 6.66f,
	val c: Any? = null
)

val cl = TestCl()
L(cl, cl.c)
```
output:
```
MainActivity.onCreate(42)	TestCl(a=some string, b=6.66, c=null) **** __NULL__
```
The class will be printed with all its elements, null elements will print `__NULL__`
Empty elements will print `__EMPTY__`
<br>
**add as many arguments as you want**
```
L(cl.a, cl.b, cl.c)
```
```
MainActivity.onCreate(43)	some string **** 6.66 **** __NULL__
```
passing no arguments will log the calling method

```
L()
```
output:
```
MainActivity.onCreate(44)	(1341) Instrumentation.callActivityOnCreate
```
<br>

**Mr.Log can also print any iterator, just pass your list and that's it!**
```
L(listOf(cl.a, cl.b, cl.c, cl, null, ""))
```
output:
```
MainActivity.onCreate(44)	[some string, 6.66, __NULL__, TestCl(a=some string, b=6.66, c=null), __NULL__, __EMPTY__]
```
<br>

**Mr.Log can print the stacktrace from any Throwable**
```
val ex = Exception("exception message")
L(ex)
L("with message", ex)
// or, to print as an error
MrLog.e(ex)
```
output:
```
MainActivity.onCreate(40)	EXCEPTION ON CLASS: MainActivity  : exception message **** java.lang.Exception: exception message at your.package.name.MainActivity.onCreate(MainActivity.kt:36)
at android.app.Activity.performCreate(Activity.java:8054)
at android.app.Activity.performCreate(Activity.java:8034)
.... etc ....																										
```
