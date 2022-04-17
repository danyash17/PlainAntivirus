package com.example.desktopantivirus.mapper;

import java.util.LinkedList;
import java.util.List;

public class ByteSequenceMapper {
    public List<Byte> map(String str) {
        List<Byte> result = new LinkedList<>();
        List<String> strBytes = List.of(str.replaceAll("\\[", "")
                .replaceAll("\\]", "").replaceAll(" ", "").split(","));
        for (String strByte : strBytes) {
            if(strByte.matches("-?\\d+")){
                result.add(Byte.valueOf(strByte));
            }
        }
        return result;
    }
}
