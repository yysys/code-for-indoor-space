package dk.aau.cs.idq.utilities;

public class SizeOfObject extends SizeOf {

	@Override
	protected Object newInstance() {
		return new Object();
	}

	public static void main(String[] args) throws Exception {
		SizeOf sizeOf = new SizeOfObject();
		System.out.println("��ռ�ڴ棺" + sizeOf.size() + "�ֽ�");
	}
}