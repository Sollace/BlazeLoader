package com.blazeloader.bl.obf;

import net.acomputerdog.OBFUtil.table.DirectOBFTableSRG;
import net.acomputerdog.OBFUtil.table.TargetTypeMap;
import net.acomputerdog.OBFUtil.util.TargetType;
import net.acomputerdog.core.java.Patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blazeloader.util.JSArrayUtils;

/**
 * BlazeLoader OBFTable that allows converting stored data into BLOBFs.
 * Provided methods automatically cache calls, so repeated calls with the same parameters will return the same BLOBF object.
 */
public class BLOBFTable extends DirectOBFTableSRG {
	private static final Pattern DESCRIPTOR_MATCHER = Pattern.compile(Patterns.DESCRIPTOR_PARAMETER);
	
	private int size = 0;
	
    private final TargetTypeMap<Map<String, BLOBF>> obfNameMap = new TargetTypeMap<Map<String, BLOBF>>();
    private final TargetTypeMap<Map<String, BLOBF>> srgNameMap = new TargetTypeMap<Map<String, BLOBF>>();
    private final TargetTypeMap<Map<String, BLOBF>> mcpNameMap = new TargetTypeMap<Map<String, BLOBF>>();

    public BLOBFTable() {
        super();
        for (TargetType type : TargetType.values()) {
            obfNameMap.put(type, new HashMap<String, BLOBF>());
            srgNameMap.put(type, new HashMap<String, BLOBF>());
            mcpNameMap.put(type, new HashMap<String, BLOBF>());
        }
    }
         
    public BLOBF getConstructor(String obfClass, OBFLevel level, String... parameterClasses) {
    	String descript = "";
    	for (int i = 0; i < parameterClasses.length; i++) {
    		descript += parameterClasses[i];
    	}
    	BLOBF obf = getMapping(level).get(TargetType.CONSTRUCTOR).get(obfClass + ".<init> (" + descript + ")V");
    	if (obf == null) {
    		return recordConstructor(obfClass, level, parameterClasses);
    	}
    	return obf;
    }
    
    private BLOBF recordConstructor(String obfClass, OBFLevel level, String... parameterClasses) {
		String srg = getSRGFromType(obfClass, TargetType.CLASS, level) + ".<init> (";
		String obfsc = getObfFromType(obfClass, TargetType.CLASS, level) + ".<init> (";
		String mcp = getMCPFromType(obfClass, TargetType.CLASS, level) + ".<init> (";
		for (int i = 0; i < parameterClasses.length; i++) {
			 //Only try to parse things we know. i.e. Minecraft classes
			String trimmed = parameterClasses[i].substring(1, parameterClasses[i].length() - 1);
			if (parameterClasses[i].endsWith(";") && hasType(trimmed, TargetType.CLASS, level)) {
    			srg += "L" + getSRGFromType(trimmed, TargetType.CLASS, level) + ";";
    			obfsc += "L" + getMCPFromType(trimmed, TargetType.CLASS, level) + ";";
    			mcp += "L" + getMCPFromType(trimmed, TargetType.CLASS, level) + ";";
			} else {
				srg += parameterClasses[i];
				obfsc += parameterClasses[i];
				mcp += parameterClasses[i];
			}
		}
		return recordOBF(TargetType.CONSTRUCTOR, new BLOBF(obfsc + ")V", srg + ")V", mcp + ")V"));
    }
    
    public BLOBF getBLOBF(String name, TargetType type, OBFLevel level) {
    	BLOBF result = getMapping(level).get(type).get(name);
    	if (result == null) {
    		if (type == TargetType.CONSTRUCTOR) {
		    	String obfClass = name.split(" ")[0].replace(".<init>", "");
		    	List<String> params = new ArrayList<String>();
		    	Matcher matcher = DESCRIPTOR_MATCHER.matcher(name.split(" ")[1].trim());
		    	while (matcher.find()) params.add(matcher.group());
		    	return recordConstructor(obfClass, level, params.size() > 0 ? JSArrayUtils.toArray(params) : new String[0]);
    		}
    		if (hasType(name, type, level)) {
    			result = recordOBF(type, new BLOBF(getObfFromType(name, type, level), getSRGFromType(name, type, level), getMCPFromType(name, type, level)));
    		} else {
    			throw new RuntimeException("Unrecognised Obfuscation String: " + level.toString() + "@" + name + " for TargetType: " + type.toString());
    		}
    	}
    	return result;
    }
    
    public boolean hasType(String name, TargetType type, OBFLevel level) {
    	switch (level) {
			case SRG: return hasSRG(name, type);
			case MCP: return hasDeobf(name, type);
			default: return hasObf(name, type);
		}
    }
    
    private BLOBF recordOBF(TargetType type, BLOBF obf) {
    	obfNameMap.get(type).put(obf.obf, obf);
        srgNameMap.get(type).put(obf.srg, obf);
        mcpNameMap.get(type).put(obf.name, obf);
    	return obf;
    }
    
    public TargetTypeMap<Map<String, BLOBF>> getMapping(OBFLevel level) {
    	switch (level) {
    		case SRG: return srgNameMap;
    		case MCP: return mcpNameMap;
    		default: return obfNameMap;
    	}
    }
    
    public String getObfFromType(String name, TargetType type, OBFLevel level) {
    	switch (level) {
			case SRG: return getObfFromSRG(name, type);
			case MCP: return obf(name, type);
			default: return name;
		}
    }
    
    public String getSRGFromType(String name, TargetType type, OBFLevel level) {
    	switch (level) {
			case OBF: return getSRGFromObf(name, type);
			case MCP: return getSRGFromDeObf(name, type);
			default: return name;
		}
    }
    
    public String getMCPFromType(String name, TargetType type, OBFLevel level) {
    	switch (level) {
			case SRG: return getDeObfFromSRG(name, type);
			case OBF: return getDeObfFromSRG(getSRGFromObf(name, type), type);
			default: return name;
		}
    }
    
    public void addType(String obfName, String deObfName, TargetType type) {
    	size++;
    	super.addType(obfName, deObfName, type);
    }
    
    public int size() {
    	return size;
    }
}
