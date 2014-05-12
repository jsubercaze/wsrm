package fr.tse.lt2c.satin.testdecompo;

public class DecompoResult {

	private double better, equals, ratioAverageIBR, ratioStdIBR,
			ratioAverageWASM, ratioStdWASM, areaStdIBR, areaStdWASM;

	int total;

	public DecompoResult(double better, double equals, double ratioAverageIBR,
			double ratioStdIBR, double ratioAverageWASM, double ratioStdWASM,
			double areaStdIBR, double areaStdWASM) {
		super();
		this.better = better;
		this.equals = equals;
		this.ratioAverageIBR = ratioAverageIBR;
		this.ratioStdIBR = ratioStdIBR;
		this.ratioAverageWASM = ratioAverageWASM;
		this.ratioStdWASM = ratioStdWASM;
		this.areaStdIBR = areaStdIBR;
		this.areaStdWASM = areaStdWASM;
	}

	public DecompoResult() {

	}

	public double getAreaStdIBR() {
		return areaStdIBR;
	}

	public void setAreaStdIBR(double areaStdIBR) {
		this.areaStdIBR = areaStdIBR;
	}

	public double getAreaStdWASM() {
		return areaStdWASM;
	}

	public void setAreaStdWASM(double areaStdWASM) {
		this.areaStdWASM = areaStdWASM;
	}

	public double getBetter() {
		return better;
	}

	public void setBetter(double better) {
		this.better = better;
	}

	public double getEquals() {
		return equals;
	}

	public void setEquals(double equals) {
		this.equals = equals;
	}

	public double getRatioAverageIBR() {
		return ratioAverageIBR;
	}

	public void setRatioAverageIBR(double ratioAverageIBR) {
		this.ratioAverageIBR = ratioAverageIBR;
	}

	public double getRatioStdIBR() {
		return ratioStdIBR;
	}

	public void setRatioStdIBR(double ratioStdIBR) {
		this.ratioStdIBR = ratioStdIBR;
	}

	public double getRatioAverageWASM() {
		return ratioAverageWASM;
	}

	public void setRatioAverageWASM(double ratioAverageWASM) {
		this.ratioAverageWASM = ratioAverageWASM;
	}

	public double getRatioStdWASM() {
		return ratioStdWASM;
	}

	public void setRatioStdWASM(double ratioStdWASM) {
		this.ratioStdWASM = ratioStdWASM;
	}

}
