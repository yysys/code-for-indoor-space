package dk.aau.cs.idq.utilities;

public abstract class SizeOf {

	private final Runtime s_runtime = Runtime.getRuntime();

	/**
	 *
	 * ���ฺ�𸲸Ǹ÷������ṩ���������ʵ��
	 *
	 * @return ���������ʵ��
	 */
	protected abstract Object newInstance();

	/**
	 *
	 * ����ʵ���Ĵ�С���ֽ�����
	 *
	 * @return ʵ����ռ�ڴ���ֽ���
	 * @throws Exception
	 */
	public int size() throws Exception {

		// ��������
		runGC();

		// �ṩ�����ܶࣨ10�򣩵�ʵ����ʹ����������ȷ
		final int count = 100000;
		Object[] objects = new Object[count];

		// ʵ����ǰ����ʹ�ô�С
		long heap1 = usedMemory();
		// ��ʵ����һ������
		for (int i = -1; i < count; ++i) {
			Object object = null;

			// ʵ��������
			object = newInstance();

			if (i >= 0) {
				objects[i] = object;
			} else {
				// �ͷŵ�һ������
				object = null;
				// �����ռ�
				runGC();
				// ʵ����֮ǰ����ʹ�ô�С
				heap1 = usedMemory();
			}
		}

		runGC();
		// ʵ����֮�����ʹ�ô�С
		long heap2 = usedMemory();
		final int size = Math.round(((float) (heap2 - heap1)) / count);

		// �ͷ��ڴ�
		for (int i = 0; i < count; ++i) {
			objects[i] = null;
		}
		objects = null;
		return size;
	}

	private void runGC() throws Exception {
		// ִ�ж����ʹ�ڴ��ռ�����Ч
		for (int r = 0; r < 4; ++r) {
			_runGC();
		}
	}

	private void _runGC() throws Exception {
		long usedMem1 = usedMemory();
		long usedMem2 = Long.MAX_VALUE;
		for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
			s_runtime.runFinalization();
			s_runtime.gc();
			Thread.currentThread().yield();
			usedMem2 = usedMem1;
			usedMem1 = usedMemory();
		}
	}

	/**
	 *
	 * ������ʹ���ڴ�
	 *
	 * @return ������ʹ���ڴ�
	 */
	private long usedMemory() {
		return s_runtime.totalMemory() - s_runtime.freeMemory();
	}
}