package signsocket;
import java.io.IOException;

import com.google.gson.Gson;

public class SocketMessage 
{
	public String Head;
	public int Length;
	public String Message;
	public String Package;
	public String[] m_splits;
	public StringBuilder strbuilder =  new StringBuilder();
	public String DEFAULT_SEPARATOR_CHAR="~";
	
	//private AppContext app;
	//
	// 序列化： 
	//　　JsonConvert.SerializeObject（string）； 
	//　　反序列化： 
	//　　JsonConvert.DeserializeObject（obj）； 
	/*
	 * Java可以用开源项目google-gson，
	 * 在项目中导入这个项目的第三方jar包，
	 * 然后添加引用：import com.google.gson.Gson；
	 * 就可使用以下方法： 
	 * Gson gson = new Gson();
序列化： 
　　Gson gson=new Gson（）； 
　　String s=gson.toJson（obj）； 
反序列化： 
　　Gson gson=new Gson（）； 
　　Object obj=gson.fromJson（s，Object.class）； */
	public SocketMessage(ClientRequest request, Object obj)
	{
		Gson gson = new Gson();
		String str = gson.toJson(obj);
		this.Package = request.toString() + DEFAULT_SEPARATOR_CHAR + str.length() + DEFAULT_SEPARATOR_CHAR + str;
		//CLog.w(Package, str);
		CLog.out(Package);
	}
	
	
	
    public void Split()
    {
        this.m_splits = this.Package.split(DEFAULT_SEPARATOR_CHAR);    //返回由'/'分隔的子字符串数组
        this.Message = "";
        if(this.m_splits.length == 3)
        {
            this.Head = this.m_splits[0];
            this.Length = Integer.parseInt(this.m_splits[1]);
            int length = this.m_splits[2].length();
            if(length >= this.Length)		// 粘包
            {
            	this.Message = this.m_splits[2];
            } 
            else		// 丢包
            {
            	int currLen = this.m_splits[2].length();
            	int recvLen;
            	this.strbuilder.append(this.m_splits[2].substring(0, currLen));      
            	
            	while(currLen < this.Length)
            	{
            		try {

//            			recvLen = SocketClient.instance().receiveMessage( );
//            			String newbuf = new String(SocketClient.instance().m_recvBuffer).substring(0, recvLen).trim();
//            			strbuilder.append(new String(SocketClient.instance().m_recvBuffer).substring(0, recvLen).trim());
//            			currLen += recvLen;
            			byte[] recvBuffer = new byte[1024 * 4];
            			recvLen = SocketClient.instance().receiveMessage(recvBuffer);
            			//String newbuf = new String(recvBuffer).trim();
            			//strbuilder.append(new String(recvBuffer).substring(0, recvLen).trim());
            			strbuilder.append(new String(recvBuffer).trim());

            			currLen += recvLen;
            			
            		} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
            	}
            	if(currLen > this.Length)
            	{
//            		this.Message = strbuilder.substring(0, this.Length).toString();
            		this.Message = strbuilder.toString().trim();

            	}
            	else
            	{
            		this.Message = strbuilder.substring(0, this.Length).toString();
            	}
            }
        }
        else
        {
            this.Head = this.m_splits[0]; 
        }
       //System.out.println(Message);
    }
}
