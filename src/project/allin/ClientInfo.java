package project.allin;

public class ClientInfo {
	String id;	// 닉네임
	String chractor;	// 캐릭터
	int holdingCoin;		// 보유 코인
	int bettingCoin;		// 배팅 코인
	String bettingSnail;	// 배팅한 달팽이
	
	public ClientInfo() {
		
	}
	
	public ClientInfo(String id, String chractor, int holdingCoin, int bettingCoin, String bettingSnail) {
		this.id = id;
		this.chractor = chractor;
		this.holdingCoin = holdingCoin;
		this.bettingCoin = bettingCoin;
		this.bettingSnail = bettingSnail;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getChractor() {
		return chractor;
	}


	public void setChractor(String chractor) {
		this.chractor = chractor;
	}


	public int getHoldingCoin() {
		return holdingCoin;
	}


	public void setHoldingCoin(int holdingCoin) {
		this.holdingCoin = holdingCoin;
	}


	public int getBettingCoin() {
		return bettingCoin;
	}


	public void setBettingCoin(int bettingCoin) {
		this.bettingCoin = bettingCoin;
	}


	public String getBettingSnail() {
		return bettingSnail;
	}


	public void setBettingSnail(String bettingSnail) {
		this.bettingSnail = bettingSnail;
	}
	
}
