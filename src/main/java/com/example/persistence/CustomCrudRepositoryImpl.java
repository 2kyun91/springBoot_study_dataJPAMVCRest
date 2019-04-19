package com.example.persistence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.example.dto.QWebBoard;
import com.example.dto.WebBoard;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.java.Log;

@Log
public class CustomCrudRepositoryImpl extends QuerydslRepositorySupport implements CustomWebBoard{ // QuerydslRepositorySupport 클래스는 생성자를 구현한다.
	
	public CustomCrudRepositoryImpl() {
		super(WebBoard.class);
	}

	@Override
	public Page<Object[]> getCustomPage(String type, String keyword, Pageable page) {
		log.info("====================");
		log.info("TYPE : " + type);
		log.info("KEYWORD : " + keyword);
		log.info("PAGE : " + page);
		log.info("====================");
		
		QWebBoard qWebBoard = QWebBoard.webBoard;
		 
		/*
		  * 페이징 처리
		  * 페이징 처리 시 Querydsl의 Qdomain 등을 이용할 수 있다.
		  * Tuple에는 where(), orderBy()등의 기능을 이용해서 원하는 조건을 제어할 수 있다.
		 */
		JPQLQuery<WebBoard> query = from(qWebBoard);
		
		JPQLQuery<Tuple> tuple = query.select(qWebBoard.bno, qWebBoard.title, qWebBoard.regdate);
		
		tuple.where(qWebBoard.bno.gt(0L));
		tuple.orderBy(qWebBoard.bno.desc());
		tuple.offset(page.getOffset());
		tuple.limit(page.getPageSize());
		
		List<Tuple> list = tuple.fetch(); // tuple.fetch()의 결과를 Collection에 담는다.
		
		List<Object[]> resultList = new ArrayList<>();
		
		list.forEach(t -> { // Object[] 형식으로 처리한다.
			resultList.add(t.toArray());
		});
		
		long total = tuple.fetchCount();
		
		return new PageImpl<>(resultList, page, total);
	}
	
}