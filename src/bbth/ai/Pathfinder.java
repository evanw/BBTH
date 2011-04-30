package bbth.ai;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.HashSet;

import android.graphics.PointF;

public class Pathfinder {
	ConnectedGraph m_graph;
	HashMap<PointF, AStarEntry> m_entries;
	HashSet<PointF> m_closed_set;
	HashMap<PointF, PointF> m_came_from;
	PriorityQueue<AStarEntry> m_open_set;
	private ArrayList<PointF> m_found_path;
	
	public Pathfinder(ConnectedGraph cg) {
		m_graph = cg;
		
		m_entries = new HashMap<PointF, AStarEntry>();
		for (PointF p : cg.get_graph().keySet()) {
			m_entries.put(p, new AStarEntry(p));
		}
		
		m_closed_set = new HashSet<PointF>();
		m_came_from = new HashMap<PointF, PointF>();
		m_open_set = new PriorityQueue<AStarEntry>();
		m_found_path = new ArrayList<PointF>();
	}
	
	public boolean find_path(PointF start, PointF goal) {
		return find_path(start, goal, null, 1.0f);
	}
	
	public boolean find_path(PointF start, PointF goal, Heuristic se) {
		return find_path(start, goal, se, 1.0f);
	}
	
	public boolean find_path(PointF start, PointF goal, Heuristic se, float tolerance) {
		m_closed_set.clear();
		m_came_from.clear();
		m_open_set.clear();
		m_found_path.clear();
		
		// Reset all the scores.
		for (PointF p : m_graph.get_graph().keySet()) {
			if (!m_entries.containsKey(p)) {
				m_entries.put(p, new AStarEntry(p));
			} else {
				AStarEntry e = m_entries.get(p);
				e.m_f_score = Integer.MAX_VALUE;
				e.m_g_score = Integer.MAX_VALUE;
				e.m_h_score = Integer.MAX_VALUE;
			}
		}
		
		if (!m_graph.contains(start)) {
			return false;
		}
		
		// Initialize open set.
		AStarEntry startentry = m_entries.get(start);
		startentry.m_g_score = 0;
		startentry.m_h_score = estimate_h_score(se, startentry, goal) ;
		startentry.m_f_score = m_entries.get(start).m_h_score;
		
		m_open_set.offer(startentry);
		
		while (m_open_set.size() != 0) {
			AStarEntry current = m_open_set.poll();
			
			if (get_dist(current, goal) < tolerance) {
				// We're done.
				reconstruct_path(current.m_point);
				return true;
			}
			
			m_closed_set.add(current.m_point);
			
			// Find neighbors.
			ArrayList<PointF> neighbors = m_graph.get_neighbors(current.m_point);
			if (neighbors == null) {
				continue;
			}
			
			int size = neighbors.size();
			for (int i = 0; i < size; i++) {
				PointF neighbor = neighbors.get(i);
				if (m_closed_set.contains(neighbor)) {
					continue;
				}
				
				int tentative_g_score = current.m_g_score + (int)get_dist(current, neighbor);
				
				AStarEntry nentry = m_entries.get(neighbor);
				
				// Check if this is a better score for this neighbor. If so, update it.
				if (nentry.m_g_score > tentative_g_score) {
					nentry.m_g_score = tentative_g_score;
					nentry.m_h_score = estimate_h_score(se, nentry, goal);
					nentry.m_f_score = nentry.m_g_score + nentry.m_h_score;
					
					m_open_set.remove(nentry);
					m_open_set.offer(nentry);
					m_came_from.put(neighbor, current.m_point);
				}
			}
		}
		
		return false;
	}

	private void reconstruct_path(PointF current) {
		if (m_came_from.containsKey(current)) {
			reconstruct_path(m_came_from.get(current));
		}
		m_found_path.add(current);
	}
	
	public ArrayList<PointF> get_path() {
		return m_found_path;
	}

	private int estimate_h_score(Heuristic se, AStarEntry start, PointF goal) {
		if (se == null) {
			return (int)get_dist(start, goal);
		} else {
			return se.estimate_h_score(start.m_point, goal);
		}
	}

	private float get_dist(AStarEntry current, PointF goal) {
		return (float)Math.sqrt((current.m_point.x - goal.x)*(current.m_point.x - goal.x) + (current.m_point.y - goal.y)*(current.m_point.y - goal.y));
	}
}