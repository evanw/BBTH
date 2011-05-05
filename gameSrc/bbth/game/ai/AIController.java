package bbth.game.ai;

import java.util.ArrayList;
import java.util.EnumMap;

import android.util.Log;
import bbth.engine.ai.FlockRulesCalculator;
import bbth.engine.ai.MapGrid;
import bbth.engine.ai.Pathfinder;
import bbth.game.BBTHGame;
import bbth.game.Team;
import bbth.game.units.Unit;

public class AIController {
	EnumMap<Team, ArrayList<Unit>> m_entities;
	EnumMap<Team, Integer> m_last_updated;
	EnumMap<Team, FlockRulesCalculator> m_flocks;
	
	DefensiveAI m_defensive;
	
	Team[] m_teams;
		
	private float m_fraction_to_update = 0.33f;
	
	public AIController() {
		m_defensive = new DefensiveAI();
		
		m_flocks = new EnumMap<Team, FlockRulesCalculator>(Team.class);
		m_last_updated = new EnumMap<Team, Integer>(Team.class);
    	m_entities = new EnumMap<Team, ArrayList<Unit>>(Team.class);
		
    	m_teams = Team.values();
    	
		for (Team t : m_teams) {
			m_flocks.put(t, new FlockRulesCalculator());
			m_last_updated.put(t, 0);
			m_entities.put(t, new ArrayList<Unit>());
		}
   	}
	
	public void setPathfinder(Pathfinder pathfinder, MapGrid grid) {
		m_defensive.setPathfinder(pathfinder, grid);
	}
	
	public void addEntity(Unit u) {
		m_entities.get(u.getTeam()).add(u);
		m_flocks.get(u.getTeam()).addObject(u);
	}
	
	public void removeEntity(Unit u) {
		m_entities.get(u.getTeam()).add(u);
		m_flocks.get(u.getTeam()).addObject(u);
	}
	
	public ArrayList<Unit> getEnemies(Unit u) {
		return m_entities.get(u.getTeam().getOppositeTeam());
	}
	
	public void update() {
		for (Team t : m_teams) {
			update(m_entities.get(t), m_flocks.get(t), t);
		}
	}
	
	private void update(ArrayList<Unit> entities, FlockRulesCalculator flock, Team team) {
		int size = entities.size();
		int num_to_update = (int) ((size * m_fraction_to_update)+1);
		
		if (size == 0) {
			return;
		}
		
		int last_updated = m_last_updated.get(team);
		
		if (last_updated > size-1) {
			last_updated = 0;
		}
		
		int i = last_updated;
		while (num_to_update > 0) {
			Unit entity = entities.get(i);
			
			// TODO: Use the correct AI for the individual unit.
			m_defensive.update(entity, this, flock);
			
			num_to_update--;
			
			if (i >= size-1) {
				i = 0;
			} else {
				i++;
			}
		}
		
		if (i >= size-1) {
			m_last_updated.put(team, 0);
		} else {
			m_last_updated.put(team, i);
		}
	}

	public float getWidth() {
		return BBTHGame.WIDTH; 
	}
	
	public float getHeight() {
		return BBTHGame.HEIGHT;
	}
	
	public void setUpdateFraction(float fraction) {
		if (fraction < 0) {
			fraction = 0;
		}
		m_fraction_to_update = fraction;
	}
}
