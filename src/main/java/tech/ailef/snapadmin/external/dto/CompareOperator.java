/* 
 * Spring Boot Database Admin - An automatically generated CRUD admin UI for Spring Boot apps
 * Copyright (C) 2023 Ailef (http://ailef.tech)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package tech.ailef.snapadmin.external.dto;

/**
 * A list of operators that are used in faceted search. 
 *
 */
public enum CompareOperator {
	GT {
		@Override
		public String getDisplayName() {
			return "Greater than";
		}
	},
	LT {
		@Override
		public String getDisplayName() {
			return "Less than";
		}
	},
	EQ {
		@Override
		public String getDisplayName() {
			return "Equals";
		}
	},
	STRING_EQ {
		@Override
		public String getDisplayName() {
			return "Equals";
		}
	},
	BEFORE {
		@Override
		public String getDisplayName() {
			return "Before";
		}
	},
	AFTER {
		@Override
		public String getDisplayName() {
			return "After";
		}
	},
	CONTAINS {
		@Override
		public String getDisplayName() {
			return "Contains";
		}
	};
	
	public abstract String getDisplayName();
	
	public String toString() {
		return this.name().toLowerCase();
	}
}
