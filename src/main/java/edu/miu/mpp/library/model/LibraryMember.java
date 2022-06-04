package edu.miu.mpp.library.model;

import java.io.Serializable;

final public class LibraryMember extends Person implements Serializable {
	private String memberId;

	private CheckoutRecord checkoutRecord;

	public LibraryMember(String memberId, String fname, String lname, String tel, Address add) {
		super(fname,lname, tel, add);
		this.memberId = memberId;
		this.checkoutRecord = new CheckoutRecord();
	}
	
	
	public String getMemberId() {
		return memberId;
	}

	public CheckoutRecord getCheckoutRecord() {
		return checkoutRecord;
	}

	public void setCheckoutRecord(CheckoutRecord checkoutRecord) {
		this.checkoutRecord = checkoutRecord;
	}

	@Override
	public String toString() {
		return "LibraryMember{" + "ID=" + memberId + ",name=" + getFirstName() + " " + getLastName() +
				",telephone=" + getTelephone() + ",address=" + getAddress() + "}";
	}

	private static final long serialVersionUID = -2226197306790714013L;
}
