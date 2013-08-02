
public class Stock {//Data class.

	private String name;
	private double buyPrice;
	private int shares;
	private double maxLoss;
	public Stock(String name, double buyPrice, int shares, double maxLoss) {
		this.name=name;
		this.buyPrice=buyPrice;
		this.shares=shares;
		this.maxLoss=maxLoss;
	}
	public double getMaxLoss() {
		return maxLoss;
	}
	public void setMaxLoss(double maxLoss) {
		this.maxLoss = maxLoss;
	}
	public int getShares() {
		return shares;
	}
	public void setShares(int shares) {
		this.shares = shares;
	}
	public double getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
