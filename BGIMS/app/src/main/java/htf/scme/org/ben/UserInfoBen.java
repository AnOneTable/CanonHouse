package htf.scme.org.ben;

public class UserInfoBen {
	public String UserId;
	public String UserName;
	public String UserImageUrl;
	
	
	public UserInfoBen() {
		super();
	}
	public UserInfoBen(String userId, String userName, String userImageUrl) {
		super();
		UserId = userId;
		UserName = userName;
		UserImageUrl = userImageUrl;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getUserImageUrl() {
		return UserImageUrl;
	}
	public void setUserImageUrl(String userImageUrl) {
		UserImageUrl = userImageUrl;
	}
	@Override
	public String toString() {
		return "UserInfoBen [UserId=" + UserId + ", UserName=" + UserName
				+ ", UserImageUrl=" + UserImageUrl + "]";
	}
	
}
