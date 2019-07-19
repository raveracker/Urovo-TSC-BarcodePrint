package com.example.tscsample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.device.scanner.configuration.Triggering;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tscdll.TSCActivity;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends Activity {

	TSCActivity TscDll = new TSCActivity();
	
	private Button test;

//	private ListView lstvw;
//	private ArrayAdapter aAdapter;
	private BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
	//Scan device
	private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//default action
	private ImageView scanButton;
	private TextView barcode_result, decode_length;
	private EditText scanResult;
	private ScanManager mScanManager;
	int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
	int[] idmodebuf = new int[]{PropertyID.WEDGE_KEYBOARD_ENABLE, PropertyID.TRIGGERING_MODES};
	String[] action_value_buf = new String[]{ScanManager.ACTION_DECODE, ScanManager.BARCODE_STRING_TAG};
	int[] idmode;

	ArrayList<BlutoothList> blutoothListArrayList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


			//TODO : SCAN
		mScanManager = new ScanManager();
		mScanManager.openScanner();

		action_value_buf = mScanManager.getParameterString(idbuf);
		idmode = mScanManager.getParameterInts(idmodebuf);
		scanResult = (EditText) findViewById(R.id.scanResult);

		decode_length = (TextView) findViewById(R.id.length_result);
		barcode_result = (TextView) findViewById(R.id.barcode_result);
		scanButton = (ImageView) findViewById(R.id.scanButton);
		scanButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction();
				switch (action) {
					case MotionEvent.ACTION_DOWN:
						//resetStatusView();
						scanResult.requestFocus();
						if(idmode[1] == Triggering.CONTINUOUS.toInt()) {
							scanButton.setBackgroundResource(R.drawable.scan_button_down);
                        /*if(vibrator != null)
                            vibrator.vibrate(VIBRATE_DURATION);*/
							mScanManager.startDecode();
						} else {
							scanButton.setBackgroundResource(R.drawable.scan_button_down);
                        /*if(vibrator != null)
                            vibrator.vibrate(VIBRATE_DURATION);*/
							mScanManager.startDecode();
						}
						break;
					case MotionEvent.ACTION_UP:
						if(idmode[1] == Triggering.CONTINUOUS.toInt()) {

						} else {
							mScanManager.stopDecode();
						}
						scanButton.setBackgroundResource(R.drawable.scan_button);
						break;
				}
				return true;
			}
		});
		scanResult.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_UP)
				{
					barcode_result.setText("" + scanResult.getText());
					decode_length.setText("" + scanResult.getText().length());
					scanResult.setText("");
					scanResult.requestFocus();

					Log.d("Data::::::::::::::::::",scanResult.getText().toString());
					printData(barcode_result.getText().toString());
					return true;
				}
				return false;
			}
		});
		
		
	}

	private void viewPrinterList() {
		// Create custom dialog object
		final Dialog dialog = new Dialog(MainActivity.this);
		// Include dialog.xml file
		dialog.setContentView(R.layout.dialog_printer_list);
		// Set dialog title
		dialog.setTitle("Bluetooth List");

		// set values for custom dialog components - text, image and button
		ListView listView = (ListView) dialog.findViewById(R.id.deviceList);

		dialog.show();

        CustomListAdapter customListAdapter = new CustomListAdapter(this, blutoothListArrayList);
        listView.setAdapter(customListAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Log.d("Click::::",list.get(position).toString());
//				TscDll.openport("00:19:0E:A4:41:A4");
				TscDll.openport(blutoothListArrayList.get(position).getId());

				final ProgressDialog progress=new ProgressDialog(MainActivity.this);
				progress.setMessage("Please wait......");
//				progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progress.setIndeterminate(true);

				progress.show();

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (TscDll.IsConnected){
							Toast.makeText(MainActivity.this,"Printer Connected",Toast.LENGTH_SHORT).show();
						}else {
							Toast.makeText(MainActivity.this,"Please connect your printer..",Toast.LENGTH_SHORT).show();
						}
						progress.dismiss();
						dialog.dismiss();
					}
				}, 5000);
			}
		});

	}

	//TODO : SCAN
	private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
			int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
			byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
			String result = intent.getStringExtra(action_value_buf[1]);

			if(result != null) {
				Log.d("Data::::::::::::::::::1",result);
				barcode_result.setText("" + result);
				decode_length.setText("" + result.length());

			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == 138 || keyCode == 120 || keyCode == 520 || keyCode == 521 || keyCode == 522) {
			scanResult.requestFocus();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mScanReceiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter();
		action_value_buf = mScanManager.getParameterString(idbuf);
		filter.addAction(action_value_buf[0]);
		registerReceiver(mScanReceiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
			case R.id.menu_printer_setup:
				if(bAdapter==null){
					Toast.makeText(getApplicationContext(),"Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
				}
				else {
					Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
					blutoothListArrayList = new ArrayList();
					if (pairedDevices.size() > 0) {
						for (BluetoothDevice device : pairedDevices) {
							BlutoothList blutoothList = new BlutoothList();
							blutoothList.setName(device.getName());
							blutoothList.setId(device.getAddress());
							blutoothListArrayList.add(blutoothList);
						}
					}

					if (blutoothListArrayList != null && !blutoothListArrayList.isEmpty()){
						viewPrinterList();
					}else {
						Toast.makeText(getApplicationContext(),"No device available.", Toast.LENGTH_SHORT).show();
					}

				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void printData(String value){

		if (TscDll.IsConnected){
			//String status = TscDll.printerstatus(300);
			TscDll.clearbuffer();
			TscDll.sendcommand("GAPDETECT [0,0]");
			TscDll.setup(50, 20, 4, 10, 1, 3, 0);
			TscDll.sendcommand("SET TEAR ON\n");
			TscDll.sendcommand("SET COUNTER @1 1\n");
//			TscDll.sendcommand("@1 = \"\"\n");
//			TscDll.sendcommand("TEXT 100,300,\"3\",0,1,1,@1\n");
			TscDll.barcode(70, -30, "128", 80, 1, 0, 1, 12, value);
//		TscDll.printerfont(100, 250, "3", 0, 1, 1, value);
			TscDll.printlabel(1, 1);
//			TscDll.closeport(5000);
		}else {
			Toast.makeText(MainActivity.this, "Please connect your printer..",Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
//		TscDll.closeport();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		TscDll.closeport();
	}
}
