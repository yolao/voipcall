package call;

public class CallUi {
	private static CallUiAdapter instance = null;

	public static void register(CallUiAdapter instance) {
		CallUi.instance = instance;
	}

	public static void openCall(Contact contact) {
		instance.openCall(contact);
	}

	public static void openChat(Contact contact) {
		instance.openChat(contact);
	}

	public static interface CallUiAdapter {

		public abstract void openCall(Contact contact);

		public abstract void openChat(Contact contact);

	}

}
