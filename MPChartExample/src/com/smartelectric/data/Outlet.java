package com.smartelectric.data;


public class Outlet {
	private int id;
	private String outletid;
	private String outletname;
	private double power;
	private double watt;
	private double unit;
	private int limit;
	private String date_time;
	
	public Outlet(){}

	public Outlet(int id, String outletid, String outletname, double power, int limit, String date_time, double watt, double unit) {
		super();
		this.id = id;
		this.outletname = outletname;
		this.outletid = outletid;
		this.power = power;
		this.limit = limit;
		this.date_time = date_time;
		this.watt = watt;
		this.unit = unit;	
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOutletname() {
		return outletname;
	}

	public void setOutletname(String outletname) {
		this.outletname = outletname;
	}

	public String getOutletID() {
		return outletid;
	}

	public void setOutletID(String outletid) {
		this.outletid = outletid;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getDate_time(){
		return date_time;
	}
	public void setDate_time(String date_time){
		this.date_time = date_time;
	}
	public double getWatt(){
		return watt;
	}
	public void setWatt(double watt){
		this.watt = watt;
	}
	public double getUnit(){
		return unit;
	}
	public void setUnit(double unit){
		this.unit = unit;
	}
}
