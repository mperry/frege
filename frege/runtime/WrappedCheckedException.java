/**
 * 
 */
package frege.runtime;

/**
 * <p>Wrapper for Checked Exceptions</p>
 * 
 * Because we cannot simply re-throw checked exceptions,
 * we must wrap checked exceptions when we catch them.
 * 
 * @author ingo
 */
public class WrappedCheckedException extends Undefined {
	/**
	 * generated by eclipse
	 */
	private static final long serialVersionUID = 6177899659489453430L;

	/**
	 * Construct a wrapped exception from a Throwable.
	 */
	public WrappedCheckedException(Throwable cause) {
		super("", cause);
	}
	
	
	/**
	 * Everything that is an  unchecked exception 
	 * (subtype of {@link java.lang.RuntimeException}) needs no wrapping. 
	 */
	public final static RuntimeException wrapIfNeeded(final RuntimeException ex) {
		return ex;
	}
	
	/**
	 * Everything that is an  error 
	 * (subtype of {@link java.lang.Error}) needs no wrapping. 
	 */
	public final static Error wrapIfNeeded(final Error ex) {
		return ex;
	}
	
	/**
	 * Construct a wrapped exception from a checked exception 
	 * 
	 */
	public final static WrappedCheckedException wrapIfNeeded(final Throwable ex) {
		return new WrappedCheckedException(ex);
	}
	
	/**
	 * <p>Run an IO action and invoke the handler on exceptions.</p>
	 * <p>The frege type of this function is</p>
	 * <code>native catch :: Class e -> IO a -> (e -> IO a) -> IO a</code>  
	 */
	public static Object doCatch(Class<?> cls,
			Lambda action, Lambda handler) {

		try {
			// System.out.println("entering try for " + cls.getName());
			return action.apply(0).result().<Object>forced();
		} catch (WrappedCheckedException e) {
			final Throwable exc = e.getCause();
			// System.out.println("entering catch for Wrapped with " + exc.getClass().getName());
			if (cls.isInstance(exc)) {
				return handler.apply(exc).apply(0).result().<Object> forced();
			}
			throw e;	// go to next catch, if any
		} catch (Throwable exc) {
			// System.out.println("entering catch for " + exc.getClass().getName());
			if (cls.isInstance(exc)) {
				return handler.apply(exc).apply(0).result().<Object> forced();
			}
			throw exc;	// go to next catch, if any
		}
//		finally {
//			System.out.println("leaving try for " + cls.getName());
//		}
	}
	
	/**
	 * <p>Run an IO action but make sure another one is run, even if the first one
	 * is interrupted.</p>
	 */
	public static Object doFinally(Lambda result, Lambda after) {
		final Object r;
		try {
			r = result.apply(0).result().<Object>forced();
		}
		finally  {
			after.apply(0).result().<Object>forced();
		}
		return r;
	}
	
	/**
	 * <p> Throw exception from monadic code.</p>
	 */
	public static void throwST(Throwable t) {
		throw wrapIfNeeded(t);
	}
}