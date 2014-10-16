package org.yaxim.androidclient.dialogs;

import org.yaxim.androidclient.XMPPRosterServiceAdapter;
import org.yaxim.androidclient.data.ChatRoomHelper;
import org.yaxim.androidclient.exceptions.YaximXMPPAdressMalformedException;
import org.yaxim.androidclient.util.XMPPHelper;
import org.yaxim.androidclient.MainWindow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.yaxim.androidclient.R;

public class EditMUCDialog extends AlertDialog implements
		DialogInterface.OnClickListener, TextWatcher {

	private MainWindow mMainWindow;
	private XMPPRosterServiceAdapter mServiceAdapter;

	private Button okButton;
	private EditText mRoomJID;
	private EditText mNickName;
	private EditText mPassword;

	public EditMUCDialog(MainWindow mainWindow,
			XMPPRosterServiceAdapter serviceAdapter) {
		super(mainWindow);
		mMainWindow = mainWindow;
		mServiceAdapter = serviceAdapter;

		setTitle("Chat Room Configuration"); //TODO i18n

		LayoutInflater inflater = (LayoutInflater) mainWindow
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View group = inflater.inflate(R.layout.muc_new_dialog, null, false);
		setView(group);

		mRoomJID = (EditText)group.findViewById(R.id.muc_new_jid);
		mNickName = (EditText)group.findViewById(R.id.muc_new_nick);
		mPassword = (EditText)group.findViewById(R.id.muc_new_pw);

		setButton(BUTTON_POSITIVE, mainWindow.getString(android.R.string.ok), this);
		setButton(BUTTON_NEGATIVE, mainWindow.getString(android.R.string.cancel),
				(DialogInterface.OnClickListener)null);

	}
	public EditMUCDialog(MainWindow mainWindow,
			XMPPRosterServiceAdapter serviceAdapter, String roomJID) {
		this(mainWindow, serviceAdapter);
		ChatRoomHelper.RoomInfo ri = ChatRoomHelper.getRoomInfo(mMainWindow, roomJID);
		mRoomJID.setText(roomJID);
		mRoomJID.setEnabled(false);
		mNickName.setText(ri.nickname);
		mPassword.setText(ri.password);
	}

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		okButton = getButton(BUTTON_POSITIVE);
		afterTextChanged(mRoomJID.getText());

		mRoomJID.addTextChangedListener(this);
		mNickName.addTextChangedListener(this);
	}

	public void onClick(DialogInterface dialog, int which) {
		ChatRoomHelper.addRoom(mMainWindow,
				mRoomJID.getText().toString(),
				mPassword.getText().toString(),
				mNickName.getText().toString());
		ChatRoomHelper.syncDbRooms(mMainWindow);
	}

	public void afterTextChanged(Editable s) {
		try {
			XMPPHelper.verifyJabberID(mRoomJID.getText());
			okButton.setEnabled(mNickName.getText().length() > 0);
			mRoomJID.setError(null);
		} catch (YaximXMPPAdressMalformedException e) {
			okButton.setEnabled(false);
			if (s.length() > 0)
				mRoomJID.setError(mMainWindow.getString(R.string.Global_JID_malformed));
		}
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {}
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
