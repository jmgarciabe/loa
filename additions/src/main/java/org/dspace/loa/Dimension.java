package org.dspace.loa;

import java.sql.SQLException;
import java.util.List;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class Dimension extends DSpaceObject {
	
	private int ID;
	
	private String name;
	
	private int layerID;
	
	/** The row in the table representing this object */
    private final TableRow myRow;
	
	/**
     * Construct a Dimension from a given context and tablerow
     * 
     * @param context
     * @param row
     */
    Dimension(Context context, TableRow row) throws SQLException
    {
        super(context);

        // Ensure that my TableRow is typed.
        if (null == row.getTable())
            row.setTable("dimension");

        myRow = row;
        ID = row.getIntColumn("dimension_id");
        name = row.getStringColumn("dimension_name");
        layerID = row.getIntColumn("layer_id");

        // Cache ourselves
        context.cache(this, row.getIntColumn("dimension_id"));

        clearDetails();
    }
    
    /**
     * Inserts a new weight in DB attached to a dimension  
     * 
     * @param context
     *            DSpace context object
     * @param dimID
     * @param layID
     * @param itemID
     * @param dimWeight
     */
    public static void addDimensionWeight(Context context, int dimID, int layID, int itemID, String dimWeight) 
    		throws SQLException
    {
    	try
    	{
            //Verificar si ya existe la fila y actualizarla o crearla
    		TableRow row;
            String query = "select * from dimension_weighting where layer_id = ?"
            			+ " and dimension_id = ? and item_id = ? ";
            
            row = DatabaseManager.querySingleTable(context,"dimension_weighting", query, layID, dimID, itemID);
            if(row == null){
            	row = DatabaseManager.create(context, "dimension_weighting");
            	row.setColumn("layer_id", layID);
                row.setColumn("dimension_id", dimID);
                row.setColumn("item_id", itemID);
            
            }
            row.setColumn("admin_weight", dimWeight);
            DatabaseManager.update(context, row);
            // Make sure all changes are committed
            context.commit();
    	}
    	catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    /**
     * Deletes all assessment weights attached to an item in DB 
     * 
     * @param context
     *            DSpace context object
     */
    public static int DeleteAssessWeights(Context context, int itemID) 
    		throws SQLException
    {
    	int rowsAffected = 0;
    	
    	String dbquery = "DELETE FROM dimension_weighting " + 
        		"WHERE item_id = ? ";
    	
    	try
    	{   
            // Deletes rows with data from frontend
            rowsAffected = DatabaseManager.updateQuery(context, dbquery, itemID);
            
            // Make sure all changes are committed
            context.commit();
    	}
    	catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return rowsAffected;
    }
    
    
	/**
     * Finds all dimensions attached to a specific layer - assumes name is unique
     * 
     * @param context
     * @param layerName
     * 
     * @return array of all dimensions by a specific layer
     */
    public static Dimension[] findByLayer(Context context, String layerName)
            throws SQLException
    {
    	String dbquery = "SELECT d.*,ld.layer_id FROM dimension d " +
        		"INNER JOIN layer2dimension ld ON ld.dimension_id=d.dimension_id " +   
        		"AND ld.layer_id = (select layer_id from layer where layer_name= ?) ";
    	
    	TableRowIterator rows = DatabaseManager.query(context, dbquery, layerName);

        try
        {
            List<TableRow> dRows = rows.toList();

            Dimension[] dimensions = new Dimension[dRows.size()];

            for (int i = 0; i < dRows.size(); i++)
            {
                TableRow row = dRows.get(i);

                // First check the cache
                Dimension fromCache = (Dimension) context.fromCache(Dimension.class, row
                        .getIntColumn("dimension_id"));    
                dimensions[i] = new Dimension(context, row);

                if (fromCache != null)
                    dimensions[i] = fromCache;
                else
                    dimensions[i] = new Dimension(context, row);
            }

            return dimensions;
        }
        finally
        {
            if (rows != null)
                rows.close();
        }
    }
    
    /**
     * Finds dimension name taking into account its ID
     * 
     * @param context
     * @param dimID
     * 
     * @return dimension name string for a specific dimension ID
     */
    public static String findNameByID(Context context, int dimID)
            throws SQLException
    {
    	String dbquery = "SELECT dimension_name FROM dimension " +   
        		"WHERE dimension_id = ? ";
    	
    	TableRow row = DatabaseManager.querySingle(context, dbquery, dimID);

        try
        {
        	String dimensionName = row.getStringColumn("dimension_name");
            return dimensionName;
        }
        finally
        {
            
        }
    }
    
    /**
     * updates dimension weight in DB attached to an item  
     * 
     * @param context
     *            DSpace context object
     * @param dimWghtID
     * @param itemID
     * @param weight
     */
    public static void updateDimensionWeight(Context context, int dimWghtID,
			int itemID, String weight) {
		// TODO Auto-generated method stub
		String dbquery = "SELECT * FROM dimension_weighting " +
        		"WHERE dimension_weighting_id = ? " + 
        		"AND item_id = ? ";

    	try
    	{
    		TableRow updateable = DatabaseManager.querySingle(context, dbquery, dimWghtID, itemID);
    		updateable.setTable("dimension_weighting");
    		updateable.setColumn("admin_weight", weight);
            
            // Save changes to the database
            DatabaseManager.update(context, updateable);
            
            // Make sure all changes are committed
            context.commit();
    	}
    	catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    /**
     * updates expert weight in DB attached to a dimension  
     * 
     * @param context
     *            DSpace context object
     * @param dimWghtID
     * @param itemID
     * @param weight
     */
    public static void updateExpertWeight(Context context, int dimWghtID,
			int itemID, int weight) {
		// TODO Auto-generated method stub
		String dbquery = "update dimension_weighting set expert_weight = ?" +
        		"where dimension_weighting_id = ? " +   
        		"and item_id = ? ";
		try
    	{
    		int numOfRowsUpdated =DatabaseManager.updateQuery(context, dbquery, weight, dimWghtID, itemID);
    	    context.commit();
    	}
    	catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	public int getID() {
		return ID;
	}
	
	
	public void setID(int iD) {
		ID = iD;
	}
	
	public int getLayerID() {
		return layerID;
	}

	public void setLayerID(int layerID) {
		this.layerID = layerID;
	}
	
	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}


	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public String getHandle() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void update() throws SQLException, AuthorizeException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateLastModified() {
		// TODO Auto-generated method stub
		
	}

}
