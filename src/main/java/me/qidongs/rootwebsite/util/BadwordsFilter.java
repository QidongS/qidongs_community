package me.qidongs.rootwebsite.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class BadwordsFilter {


    private static Logger logger =  LoggerFactory.getLogger(BadwordsFilter.class);

    private static final String REPLACEMENT="***";

    private TreeNode root = new TreeNode();

    @PostConstruct
    public void init(){
        try(
                InputStream inputStream= this.getClass().getClassLoader().getResourceAsStream("banned_words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ){
            String keyword;
            while ((keyword=reader.readLine())!=null){
               //put the word into the tree
               this.addKeyword(keyword);
            }

        }catch (IOException e){
            logger.error("failed to load banned words"+e.getMessage());
        }
    }

    //add a word into the tree
    private void addKeyword(String keyword){
        TreeNode tempNode = root;
        for (int i = 0 ; i<keyword.length();i++){
            char c = keyword.charAt(i);
            TreeNode subNode =tempNode.getSubNode(c);
            if (subNode == null){
                subNode = new TreeNode();
                tempNode.addSubNode(c,subNode);

            }
            tempNode=subNode;

            //setting end point
            if(i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }

        }
    }

    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        TreeNode tempNode = root;
        int begin =0;
        int position =0;
        StringBuilder sb = new StringBuilder();

        while (position<text.length()){
            char c = text.charAt(position);

            //jump symbol
            if (isSymbol(c)){
                if(tempNode==root){
                    sb.append(c);
                    begin++;

                }

                position++;
                continue;
            }

            //check subnode
            tempNode = tempNode.getSubNode(c);
            if (tempNode==null){
                //not a bad word
                sb.append(text.charAt(begin));

                position=++begin;

                tempNode = root;
            }else if(tempNode.isKeywordEnd()){
                //find a bad word
                //replace the substring
                sb.append(REPLACEMENT);

                begin=++position;

                tempNode=root;
            }else {
                //not found yet
                position++;
            }


        }

        sb.append(text.substring(begin));
        return sb.toString();


    }

    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c>0X9FFF);
    }

    private class TreeNode{

        private boolean isKeywordEnd=false;

        //children
        private Map<Character,TreeNode> subnodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c, TreeNode node){
            subnodes.put(c,node);
        }

        public TreeNode getSubNode(Character c){
            return subnodes.get(c);
        }
    }


}
