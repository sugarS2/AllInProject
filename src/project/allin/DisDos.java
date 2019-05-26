package project.allin;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class DisDos {
	DataInputStream dis;
	DataOutputStream dos;
	
	public DisDos(DataInputStream dis, DataOutputStream dos) {
		super();
		this.dis = dis;
		this.dos = dos;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}
	
	
}
