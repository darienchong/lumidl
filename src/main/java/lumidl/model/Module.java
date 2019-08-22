package lumidl.model;

import com.google.gson.annotations.SerializedName;

/**
 * Encapsulates all module-related data.
 * @author dongyu
 *
 */
public class Module {
	public String id;
	public String createdDate;
	public String creatorID;
	public String lastUpdatedDate;
	public String lastUpdatedBy;
	public String name;
	public String startDate;
	public String endDate;
	public boolean publish;
	public String parentID;
	public String resourceID;
	public Access access;
	public String courseName;
	public int facultyCode;
	public int departmentCode;
	public String term;
	public String acadCareer;
	public boolean courseSearchable;
	public String allowAnonFeedback;
	public boolean displayLibGuide;
	public String copyFromID;
	@SerializedName("13")
	public int thirteen;
	public boolean enableLearningFlow;
	public boolean usedNusCalendar;
	public boolean isCorporateCourse;	

	@Override
	public String toString() {
		return name + " (" + courseName + ")";
	}
}
