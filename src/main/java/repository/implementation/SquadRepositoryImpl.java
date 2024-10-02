package repository.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.DatabaseConnection;
import model.Squad;
import repository.interfaces.SquadRepository;

public class SquadRepositoryImpl implements SquadRepository {
	
	private static final String GET_ALL_SQUADS_QUERY = "SELECT * FROM squad LIMIT ? OFFSET ? " ;
	
	@Override
	public List<Squad> getAllSquads(int page , int pageSize){
		
		List<Squad> squads = new ArrayList<>();
		
		int pagination = (page-1) * pageSize;
		
		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement stmt = connection.prepareStatement(GET_ALL_SQUADS_QUERY)){
					
					stmt.setInt(1,pageSize);
					stmt.setInt(2,pagination);
					
					ResultSet rs = stmt.executeQuery();
		          
		          while(rs.next()) {
		        	  Squad squad = new Squad();
		        	  squad.setId(rs.getLong("id"));
		        	  squad.setName(rs.getString("name"));
		        	  squads.add(squad);
		      
		          }
		
				}catch (SQLException e) {
					e.printStackTrace();
				}
		return squads;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public Squad getSquadById(long id) {
		return null ;
		
	}
	
	@Override
	public void addSquad(Squad squad) {
		
	}
	
	@Override
	public void updateSquad(Squad squad) {
		
	}
	
	@Override
	public void deleteSquad(long id) {
		
	}
	

}
