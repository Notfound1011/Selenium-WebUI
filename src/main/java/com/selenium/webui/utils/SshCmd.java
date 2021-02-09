package com.selenium.webui.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author shiyuyu
 * exec command via ssh
 */
public class SshCmd {

	private static final Logger logger = Logger.getLogger(SshCmd.class);
    
    /**
     * @param hostname 主机名
     * @param username 用户名
     * @param password 密码
     * @param sshCommands 命令
     */
	public static String sshCmdGetResponse(String hostname, String username, String password, String sshCommands) {
		String line = "";
		
		Connection conn = new Connection(hostname);
		Session ssh = null;
		StringBuffer sb = new StringBuffer();
		try {
			conn.connect();

			boolean isconn = conn.authenticateWithPassword(username, password);
			if (!isconn) {
				logger.debug("connect fail");
			} else {
				logger.info("SSH to Switch " + hostname +" OK");
				ssh = conn.openSession();
				ssh.execCommand(sshCommands);
				
                InputStream is = new StreamGobbler(ssh.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                
                while (true)
    			{
    				line = brs.readLine();
    				logger.debug(line);
    				if (line == null)
    					break;    				
    				sb.append(line);    				
    			}
                logger.debug("ExitCode: " + ssh.getExitStatus());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ssh.close();
			conn.close();
		}
		
		line = sb.toString();
		//logger.debug("[sshSwitchAndGet] line = " + line);
		String responseStr = line;

		//根据某一个字符截取字符串
        if(line.indexOf("=") > 0)
		    responseStr =line.substring(0, line.indexOf("=")-1);
		
		logger.debug("[sshCmdAndGetResponse] Switch response from SSH : " + responseStr);
		return responseStr;
	}
}
