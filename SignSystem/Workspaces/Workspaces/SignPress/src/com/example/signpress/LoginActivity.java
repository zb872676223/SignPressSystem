package com.example.signpress;

//import android.support.v7.app.ActionBarActivity;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;








import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener; 
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.hwj.signpress.R;
import signdata.Employee;
import signdata.User;
import signsocket.NetManager;
import signsocket.SocketClient;

/*
 * 原因： 
android.os.NetworkOnMainThreadException是说不要在主线程中访问网络，
这个是android3.0版本开始就强制程序不能在主线程中访问网络，要把访问网络放在独立的线程中。

在开发中，为了防止访问网络阻塞主线程，
一般都要把访问网络放在独立线程中或者异步线程AsyncTask中。

1、想要忽略这些强制策略问题的话，可以在onCreate()方法里面加上 
StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
StrictMode.setThreadPolicy(policy);
并在方法上加上@SuppressLint("NewApi")，重试，OK。

2、将网络访问放到单独线程中

3、将网络访问放到异步任务AsyncTask中
我们采用此方法实现
 * */

//@SuppressWarnings("deprecation")
public class LoginActivity extends Activity 
{
	
	//  用户名输入框
	private EditText editTextUsername;
	
	// 密码输入框
	private EditText editTextPassword;

	// 登录按钮
	private Button buttonLogin;
	
	//记住密码
	private CheckBox cbPassWord;
	
	private PrintWriter output ;
    
	private ProgressDialog progressBar = null;
	
	private SharedPreferences sp;
	public static Context s_context;

	
	private AppContext app;
	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);		//  设置布局文件为activity_login.xml
		
		// 得到两个EditText对象
		this.editTextUsername = (EditText)findViewById(R.id.editTextUsername);
		this.editTextPassword = (EditText)findViewById(R.id.editTextPassword);
		this.cbPassWord=(CheckBox)findViewById(R.id.cb_mima);
		this.sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE); 
		
		// 得到两个button对象
		this.buttonLogin = (Button)findViewById(R.id.buttonLogin);
		
		//StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//StrictMode.setThreadPolicy(policy);
		//以下是所加的代码
	    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() 
	        .detectDiskReads() 
	        .detectDiskWrites() 
	        .detectNetwork()
	        .penaltyLog() 
	        .build()); 
	        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder() 
	        .detectLeakedSqlLiteObjects() 
	        .detectLeakedClosableObjects() 
	        .penaltyLog() 
	        .penaltyDeath() 
	        .build());
		//NetManager.instance().init(this);
		
		NetManager.instance().init(this);;
		s_context = this;
		
		//SocketThreadManager.sharedInstance();
		
		if(sp.getBoolean("ISCHECK", false))  
        {  
          //设置默认是记录密码状态  
          cbPassWord.setChecked(true);  
          editTextUsername.setText(sp.getString("USER_NAME", ""));  
          editTextPassword.setText(sp.getString("PASSWORD", ""));  
        }
		
		// 为登录按钮绑定单击事件
		this.buttonLogin.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				progressBar = new ProgressDialog(v.getContext());
				progressBar.setCancelable(true);
				progressBar.setMessage("正在登陆");
				progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressBar.show();
				loginAsyncTask();
			}
			
			private void loginAsyncTask()
			{
		        new AsyncTask<String, Void, Object>()
		        {
		          
		          //在doInBackground 执行完成后，onPostExecute 方法将被UI 线程调用，
		          // 后台的计算结果将通过该方法传递到UI 线程，并且在界面上展示给用户.
		          @Override
				protected void onPostExecute(Object result)
		          {
		            super.onPostExecute(result);
		            //activity_main_btn1.setText("请求结果为："+result);//可以更新UI
		        	//  获取文本框中的用户名和密码
		            boolean r = isNetAvailable(LoginActivity.this);
		            if(r)
		            {
					String username = editTextUsername.getText().toString();
					String password = editTextPassword.getText().toString();
					
					if(username.equals("")||password.equals(""))
					{
						Toast.makeText(LoginActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
					}
					else
					{
					User user = new User(username, password);
			        app=(AppContext)getApplication();

					app.setUser(user);
					
	
					// 手机能联网，读socket数据
					//if (NetManager.instance().isNetworkConnected())
					{
						//SocketClient client = SocketClient.instance();
						//SocketMessage message = new SocketMessage(ClientRequest.LOGIN_REQUEST, user);
						Employee employee;
						employee = SocketClient.instance().loginRequest(user);
						if(employee==null)
						{
							Toast.makeText(LoginActivity.this, "远程服务器未响应", Toast.LENGTH_SHORT).show();
						}
 						else if(employee.Id != -1 && employee.CanSign == 1)
						{
							if(cbPassWord.isChecked())
							{
								Editor editor = sp.edit();  
			                    editor.putString("USER_NAME", username);  
			                    editor.putString("PASSWORD",password);  
			                    editor.commit(); 
							}
							
							app.setEmployee(employee);
							Intent intent = new Intent();  
							//设置Intent的class属性，跳转到SecondActivity  
							intent.setClass(LoginActivity.this, MainActivity.class);  
							//为intent添加额外的信息  
							//intent.putExtra("Usename", username);  
							//intent.putExtra("Password", password); 
							//启动Activity  
							startActivity(intent);  
							finish();
						}
						else if(employee.Id != -1 && employee.CanSign != 1)
						{
							progressBar.dismiss();
							// 使用弹窗，告诉用户没有签字权限无法登录
							Toast.makeText(LoginActivity.this, "您没有签字权限，无法登陆", Toast.LENGTH_SHORT).show();
						}
						else        
						{
							progressBar.dismiss();
							// 使用弹窗，告诉用户输入的用户名或者密码有误
							Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
						}
					}
		              }
					}
		            else
					{
		            	progressBar.dismiss();
		            	Toast.makeText(LoginActivity.this, "当前未连接网络或网络不可用", Toast.LENGTH_SHORT).show();
					}
		          }

		          private boolean isNetAvailable(Context context) {
					// TODO 自动生成的方法存根 
		        	 ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		        	 if(manager != null)
		        	 {
		             NetworkInfo info = manager.getActiveNetworkInfo();  
		        	 return (info != null && info.isAvailable());  
		        	 }
		             else
		             {
		            	 return false;
		             }
				}

				//该方法运行在后台线程中，因此不能在该线程中更新UI，UI线程为主线程
		          @Override
				protected Object doInBackground(String... params)
		          {
						 return true;
		          }

		        }.execute();
		        
		      }
		});
		
		cbPassWord.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
                if (cbPassWord.isChecked()) {  
                      
                    System.out.println("记住密码已选中");  
                    sp.edit().putBoolean("ISCHECK", true).commit();  
                      
                }else {  
                      
                    System.out.println("记住密码没有选中");  
                    sp.edit().putBoolean("ISCHECK", false).commit();  
                      
                }  
  
            }  
        });  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	/*********启动客户端方法***********/
    public void Connect()
    {   
       try 
       {
           InetAddress addr = InetAddress.getByName("10.0.213.117");//服务端手机网络IP地址，连一下wifi就可以知道
           System.out.println("客户端发出请求");
                 
           //客 户端向服务端发出连接请求
           Socket socket = new Socket(addr,6666);
           System.out.println("连接成功，socket=" + socket);
               
           //  通过该条socket通道得到输出流
           output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
           System.out.println("输出流获取成功");
           output.println(editTextUsername.getEditableText().toString());
           output.flush();
             

            
           /****/
           Intent intentdump =new Intent();
           intentdump.putExtra("username", editTextUsername.getText().toString());
           intentdump.putExtra("password", editTextPassword.getText().toString());
           //intentdump.setClass(LoginActivity.this, MainActivity.class);
           
           LoginActivity.this.startActivity(intentdump);
       }
       catch (Exception e) 
       {
           e.printStackTrace();
       }
             
    }
}
