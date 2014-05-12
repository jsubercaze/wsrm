package fr.tse.lt2c.satin.testdecompo;

public class IBRvsWASM {
	int IBR;
	int WASM;

	public IBRvsWASM(int iBR, int wASM) {
		super();
		IBR = iBR;
		WASM = wASM;
	}

	public IBRvsWASM(int wASM) {
		super();
		WASM = wASM;
	}

	public int getIBR() {
		return IBR;
	}

	public void setIBR(int iBR) {
		IBR = iBR;
	}

	public int getWASM() {
		return WASM;
	}

	public void setWASM(int wASM) {
		WASM = wASM;
	}

	public boolean isWasmBetter() {
		return WASM < IBR;
	}

	public boolean IsWasmBetterOrEqual() {
		return WASM <= IBR;
	}

	public boolean isDefined() {
		return IBR != 0;
	}

}
