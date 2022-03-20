package mosaic.scheduler.simulator.util.misc;

public abstract class Pooled<_Pooled_ extends Pooled<_Pooled_>> {

	protected Pooled(final Pool<_Pooled_> pool) {
		super();
		this.pool = pool;
	}

	protected void initialize(Object... arguments) {
		return;
	}

	protected void deinitialize() {
		return;
	}

	@SuppressWarnings("unchecked")
	protected final void finalize() {
		this.deinitialize();
		if (this.pool != null)
			this.pool.enqueue((_Pooled_) this);
	}

	private final Pool<_Pooled_> pool;
	public _Pooled_ nextPooled;

	protected static final class Pool<_Pooled_ extends Pooled<_Pooled_>> {

		public Pool(final Class<_Pooled_> clasz) {
			super();
			this.clasz = clasz;
			this.first = null;
		}

		protected final void enqueue(final _Pooled_ object) {
			if (object != null) {
				synchronized (Pooled.Pool.class) {
					object.nextPooled = this.first;
					this.first = object;
					pooled++;
				}
			}
		}

		public final _Pooled_ dequeue(final Object... arguments) {
			final _Pooled_ pooled;
			synchronized (Pooled.Pool.class) {
				if (this.first != null) {
					pooled = this.first;
					this.first = pooled.nextPooled;
					Pool.pooled--;
				} else
					pooled = null;
				count++;
				if (pooled == null)
					missed++;
			}

			final _Pooled_ object;
			if (pooled != null)
				object = pooled;
			else
				try {
					object = this.clasz.getConstructor(Pool.class).newInstance(this);
				} catch (final Exception exception) {
					throw (new Error(exception));
				}

			object.initialize(arguments);

			if ((count % 100000) == 0) {
				System.err.format("PooledBase ", " count = ", count / 1000,
						"k; ", " missed = ", missed / 1000, "k; ",
						" pooled = ", Pool.pooled / 1000, "k; ", " ",
						Math.round(((double) missed / (double) count) * 100),
						" %.");
				System.gc();
				System.runFinalization();
			}

			return (object);
		}

		private static int count;
		private static int missed;
		private static int pooled;

		final Class<_Pooled_> clasz;
		_Pooled_ first;
	}
}