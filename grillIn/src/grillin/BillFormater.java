package grillin;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

class ItemInfo{

	String itemName;
	String qty;
	String amount;

	public ItemInfo() {
	}

	@Override
	public String toString() {
		return "ItemInfo [itemName=" + itemName + ", qty=" + qty + ", amount=" + amount + "]";
	}	
}

class BillData{

	List<ItemInfo> itemList;
	String kot;
	String tableNumber;
	int totalQty = 0;
	String subTotalAmt;
	String gst;
	String cgst;
	String sgst;
	String rounfOff;
	String totalInvoiceValue;

	String header = "Grill Inn\n"
			+ "ELEMENTUM ENTERPRISES\n"
			+ "Ground Floor, Rameshwaram Plaza,\n"
			+ "Near Maruti Suzuki Showroom,\n"
			+ "Jullu Park, Hazaribag,\n"
			+ "Jharkhand- 825301\n"
			+ "Mob.9110986065\n"
			+ "GSTIN: 20AAJFE1976C1ZU\n"
			+ "----------------------------------------\n"
			+ "Type:Table\n"
			+ "Table Number: [TABLE_NUMBER]\n"
			+ "----------------------------------------\n"
			+ "Bill No. : [BILL_NUMBER]\n"
			+ "Steward:\n"
			+ "Cashier:Cashier. .\n"
			+ "Date:[DATE]\n"
			+ "Kots:[KOT]\n"
			+ "----------------------------------------\n"
			+ "[HEADING]\n"
			+ "----------------------------------------\n"
			+ "[ITEMS]"
			+ "----------------------------------------\n"
			+ "[TQTY]\n"
			+ "[STAMT]\n"
			+ "----------------------------------------\n"
			+ "[GST]\n"
			+ "[CGST]\n"
			+ "[SGST]\n"
			+ "----------------------------------------\n"
			+ "[RNDOFF]\n"
			+ "[TIV]\n"
			+ "----------------------------------------\n"
			+ "[PAY]\n"
			+ "----------------------------------------\n"
			+ "Thank you, visit again!\n"
			+ "-------------------\n"
			+ " Powered by. —  POSIST\n"
			+ "";


	String kotHeader = ""
			+ "-----------------------------\n"
			+ "Type:Table\n"
			+ "T No:[TABLE_NUMBER]\n"
			+ "-----------------------------\n"
			+ "Bill No:[BILL_NUMBER]\n"
			+ "Steward:\n"
			+ "Date:[DATE]\n"
			+ "Kot Nubmer:[KOT]\n"
			+ "-----------------------------\n"
			+ "[HEADING]\n"
			+ "-----------------------------\n"
			+ "[ITEMS]"
			+ "-----------------------------\n"
			+ "[TQTY]\n"
			+ "-----------------------------\n"
			+ "";

	public List<ItemInfo> getItemList() {
		return itemList;
	}
	public void setItemList(List<ItemInfo> itemList) {
		this.itemList = itemList;
	}
	public String getKot() {
		return kot;
	}
	public void setKot(String kot) {
		this.kot = kot;
	}
	public String getTableNumber() {
		return tableNumber;
	}
	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}
	public int getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}
	public String getSubTotalAmt() {
		return subTotalAmt;
	}
	public void setSubTotalAmt(String subTotalAmt) {
		this.subTotalAmt = subTotalAmt;
	}
	public String getGst() {
		return gst;
	}
	public void setGst(String gst) {
		this.gst = gst;
	}
	public String getCgst() {
		return cgst;
	}
	public void setCgst(String cgst) {
		this.cgst = cgst;
	}
	public String getSgst() {
		return sgst;
	}
	public void setSgst(String sgst) {
		this.sgst = sgst;
	}
	public String getRounfOff() {
		return rounfOff;
	}
	public void setRounfOff(String rounfOff) {
		this.rounfOff = rounfOff;
	}
	public String getTotalInvoiceValue() {
		return totalInvoiceValue;
	}
	public void setTotalInvoiceValue(String totalInvoiceValue) {
		this.totalInvoiceValue = totalInvoiceValue;
	}


	@Override
	public String toString() {
		return "BillData [itemList=" + itemList + ", kot=" + kot + ", tableNumber=" + tableNumber + ", totalQty="
				+ totalQty + ", subTotalAmt=" + subTotalAmt + ", gst=" + gst + ", cgst=" + cgst + ", sgst=" + sgst
				+ ", rounfOff=" + rounfOff + ", totalInvoiceValue=" + totalInvoiceValue + "]";
	}


}
public class BillFormater {

	public static void main(String[] args) {

		Properties prop = null;
		String filePath = null;
		int billNumber = 21226;
		try {
			prop = getProperties();
			System.out.println(prop);
			if(prop==null) {
				filePath = "/opt/pop/conf";
			} else {
				filePath = prop.getProperty("File.path");
			}
			
			billNumber = prop.getProperty("Bill.number.start") !=null ? 
					Integer.parseInt(prop.getProperty("Bill.number.start")) : 21226;

		} catch (Exception e2) {
			System.out.println("Properties file is not present. Taking default value in this case.");
			filePath = "/opt/pop/conf";
		}
		System.out.println("---------Program Started----------");
		System.out.println("FilePath : "+ filePath);
		System.out.println("Properties : "+ prop!=null ? prop.toString(): "Properties File not configured.");

		start :
			while(true) {
				File billFile = new File(filePath+"/BILL.txt");
				File kotFile = new File(filePath+"/KOT.txt");
				try {
					//System.out.println("Formating started.... ");

					BillData billData = new BillData();
					List<ItemInfo> itemList = new ArrayList<>();
					billData.setItemList(itemList);

					boolean isItemFound = false;
					boolean isContentFound = false;
					ItemInfo itemInfo = null;

					DecimalFormat df = new DecimalFormat();
					df.setRoundingMode(RoundingMode.HALF_UP);
					df.setMaximumFractionDigits(2);
					df.setMinimumFractionDigits(2);

					float subTotalAmt = 0;

					try (BufferedReader br = new BufferedReader(new FileReader(billFile))){
						String st;
						int line = 0;
						int itemRow = 0;
						while ((st = br.readLine()) != null) {
							line++;
							if(line==1 && st.startsWith("1.")) {
								isContentFound = true;
								System.out.println("Contents found for formating.... ");
							}else if(line==1){
								//System.out.println("Searching contents to format.... ");
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								continue start ;
							}
							if(isContentFound) {
								//Capturing Data Starts
								boolean isItem = Pattern.matches("^[0-9]{1,2}[.]\\s*", st);  
								if(isItem) {
									isItemFound = true;
									itemRow = 1;
									itemInfo = new ItemInfo();
									itemList.add(itemInfo);
								} else if(isItemFound){
									if(itemRow == 1) {
										//System.out.println("Item : " + st);
										itemInfo.itemName = formatItemName(st.trim());
									} else if(itemRow == 2) {
										//System.out.println("Qty : " + st);
										itemInfo.qty = st.trim();
										billData.setTotalQty(billData.getTotalQty() + Integer.parseInt(st.trim()));
									} else if(itemRow == 3) {
										//System.out.println("Amt : " + st);
										itemInfo.amount = df.format(Float.parseFloat(st.trim()));
										subTotalAmt = subTotalAmt + Float.parseFloat(st.trim());
										isItemFound = false;
									}

									itemRow++;
								}

								boolean isTableNumber = Pattern.matches("\\s*[t,T][0-9]{1,3}\\s*", st);  
								if(isTableNumber) {
									billData.setTableNumber(st.trim().substring(1));
								}
								boolean  iskot = Pattern.matches("\\s*[k,K][0-9]{1,3}\\s*", st);  
								if(iskot) {
									billData.setKot(st.trim().substring(1));
								}
								//Capturing Data Ends
							}
						}

						br.close();
					}

					if(isContentFound) {
						//Preparing Bill Data Starts
						billData.setSubTotalAmt(df.format(subTotalAmt));
						billData.setGst(df.format(subTotalAmt * 0.05));
						billData.setCgst(df.format(Float.parseFloat(billData.getGst())/2));
						billData.setSgst(df.format(Float.parseFloat(billData.getGst())/2));

						System.out.println(billData.getSubTotalAmt());
						System.out.println(billData.getGst());
						
						float totalAmt = Float.parseFloat(billData.getSubTotalAmt()) + Float.parseFloat(billData.getGst());

						billData.setTotalInvoiceValue(Math.floor(totalAmt) + "");
						billData.setRounfOff(df.format(Float.parseFloat(billData.getTotalInvoiceValue()) - totalAmt));
						billData.setTotalInvoiceValue(((int)Float.parseFloat(billData.getTotalInvoiceValue()))+"");
						System.out.println("BillData : " + billData);
						//Preparing Bill Data Ends


						//Printing BILL Starts
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = new Date();

						try(FileWriter fw = new FileWriter(billFile)) {

							String header = billData.header;
							header = header.replace("[HEADING]", String.format("%-17s %-6s %-6s", "Item", "Qty", "Amt"));
							String itemBillList = "";
							for(ItemInfo itemData : itemList) {
								if(itemData.itemName.contains("\n")) {
									String itemName = itemData.itemName;
									while(itemName.contains("\n")) {
										itemBillList = itemBillList + (String.format("%-17s \r\n", itemName.substring(0, itemName.indexOf("\n"))));
										itemName = itemName.substring(itemName.indexOf("\n")+1);
									}
									itemBillList = itemBillList + (String.format("%-17s %-6s %6s \r\n", itemName, itemData.qty, itemData.amount));
								}else {
									itemBillList = itemBillList + (String.format("%-17s %-6s %6s \r\n", formatItemName(itemData.itemName), itemData.qty, itemData.amount));
								}
							}

							header = header.replace("[DATE]", dateFormat.format(date));
							header = header.replace("[BILL_NUMBER]", billNumber + "");
							header = header.replace("[TABLE_NUMBER]", billData.getTableNumber());
							header = header.replace("[KOT]", billData.getKot());
							header = header.replace("[ITEMS]", itemBillList.toString());
							header = header.replace("[TQTY]", String.format("%-17s %-6s", "Total Qty:", billData.getTotalQty()));
							header = header.replace("[STAMT]", String.format("%-17s %13s", "Sub Total:", billData.getSubTotalAmt()));
							header = header.replace("[GST]", String.format("%-17s %13s", "GST@5%", billData.getGst()));
							header = header.replace("[CGST]", String.format("%12s %12s", "GST @2.5%", billData.getCgst()));
							header = header.replace("[SGST]", String.format("%12s %12s", "GST @2.5%", billData.getSgst()));
							header = header.replace("[RNDOFF]", String.format("%-17s %13s", "Round Off:", billData.getRounfOff()));
							header = header.replace("[TIV]", String.format("%-17s %11s", "Total Invoice Value", billData.getTotalInvoiceValue()));
							header = header.replace("[PAY]", String.format("%6s%-14s", "PAY:", billData.getTotalInvoiceValue()));

							fw.write(header);
							fw.flush();

						}
						//Printing BILL Ends


						//Printing KOT Starts
						try (FileWriter fw = new FileWriter(kotFile)){
							String header = billData.kotHeader;
							header = header.replace("[HEADING]", String.format("%-20s %-3s", "Item", "Qty"));
							String itemBillList = "";
							for(ItemInfo itemData : itemList) {
								if(itemData.itemName.contains("\n")) {
									String itemName = itemData.itemName;
									itemBillList = itemBillList + "* ";
									while(itemName.contains("\n")) {
										itemBillList = itemBillList + (String.format("%-20s \r\n", itemName.substring(0, itemName.indexOf("\n"))));
										itemName = itemName.substring(itemName.indexOf("\n")+1);
									}
									itemBillList = itemBillList + (String.format("%-20s %3s \r\n", itemName, itemData.qty, itemData.amount));
								}else {
									itemBillList = itemBillList + (String.format("%-20s %3s \r\n", "* "+formatItemName(itemData.itemName), itemData.qty));
								}
							}

							header = header.replace("[DATE]", dateFormat.format(date));
							header = header.replace("[BILL_NUMBER]", billNumber++ + "");
							header = header.replace("[TABLE_NUMBER]", billData.getTableNumber());
							header = header.replace("[KOT]", billData.getKot());
							header = header.replace("[ITEMS]", itemBillList.toString());
							header = header.replace("[TQTY]", String.format("%-20s %3s", "Total Qty:", billData.getTotalQty()));


							fw.write(header);
							fw.flush();
							//Printing KOT Ends

						}
						System.out.println("Formating Ends.... ");
					}

				} catch(Exception e) {
					e.printStackTrace();
					try(FileWriter fw = new FileWriter(billFile, true);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter out = new PrintWriter(bw)) {
						out.println("Got Exception : Please try again with correct format.");
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						out.println(sw.toString());
						fw.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
	}

	static String formatItemName(String itemName)
	{
		String formattedStr = "";
		if(itemName!=null && !itemName.isEmpty()) {
			String[] strArr = itemName.split("\\s+");
			//System.out.println(Arrays.toString(strArr));
			for(int i = 0; i<strArr.length ; i++) {
				formattedStr+=strArr[i];
				if(i%2==1 && i != strArr.length-1) {
					formattedStr+="\n";
				} else if(i != strArr.length-1) {
					formattedStr+=" ";
				}
			}
		}
		return formattedStr;
	}

	public static Properties getProperties() throws IOException {
		FileInputStream fis = null;
		Properties prop = null;
		try {
			fis = new FileInputStream("/opt/pop/conf/config.properties");
			prop = new Properties();
			prop.load(fis);
		} catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			fis.close();
		}
		return prop;
	}
}
