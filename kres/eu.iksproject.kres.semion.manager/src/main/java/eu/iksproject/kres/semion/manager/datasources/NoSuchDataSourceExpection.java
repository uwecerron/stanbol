package eu.iksproject.kres.semion.manager.datasources;

public class NoSuchDataSourceExpection extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int dataSourceType;
	
	public NoSuchDataSourceExpection(int dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
	
	public int getDataSourceType() {
		return dataSourceType;
	}
}
