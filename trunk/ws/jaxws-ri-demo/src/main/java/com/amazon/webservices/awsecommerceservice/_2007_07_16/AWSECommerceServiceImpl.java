/*
 * Copyright 2007 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazon.webservices.awsecommerceservice._2007_07_16;

import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.Holder;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@HandlerChain(file = "AWSECommerceServiceImplCandlerChain.xml")
@WebService(endpointInterface = "com.amazon.webservices.awsecommerceservice._2007_07_16.AWSECommerceServicePortType")
public class AWSECommerceServiceImpl implements AWSECommerceServicePortType {

    public void browseNodeLookup(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag,
            String validate, String xmlEscaping, BrowseNodeLookupRequest shared, List<BrowseNodeLookupRequest> request,
            Holder<OperationRequest> operationRequest, Holder<List<BrowseNodes>> browseNodes) {
        // TODO Auto-generated method stub

    }

    public void cartAdd(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, CartAddRequest shared, List<CartAddRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Cart>> cart) {
        // TODO Auto-generated method stub

    }

    public void cartClear(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, CartClearRequest shared, List<CartClearRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Cart>> cart) {
        // TODO Auto-generated method stub

    }

    public void cartCreate(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, CartCreateRequest shared, List<CartCreateRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Cart>> cart) {
        // TODO Auto-generated method stub

    }

    public void cartGet(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, CartGetRequest shared, List<CartGetRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Cart>> cart) {
        // TODO Auto-generated method stub

    }

    public void cartModify(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, CartModifyRequest shared, List<CartModifyRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Cart>> cart) {
        // TODO Auto-generated method stub

    }

    public void customerContentLookup(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag,
            String validate, String xmlEscaping, CustomerContentLookupRequest shared, List<CustomerContentLookupRequest> request,
            Holder<OperationRequest> operationRequest, Holder<List<Customers>> customers) {
        // TODO Auto-generated method stub

    }

    public void customerContentSearch(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag,
            String validate, String xmlEscaping, CustomerContentSearchRequest shared, List<CustomerContentSearchRequest> request,
            Holder<OperationRequest> operationRequest, Holder<List<Customers>> customers) {
        // TODO Auto-generated method stub

    }

    public void help(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            HelpRequest shared, List<HelpRequest> request, Holder<OperationRequest> operationRequest, Holder<List<Information>> information) {
        // TODO Auto-generated method stub

    }

    public void itemLookup(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, ItemLookupRequest shared, List<ItemLookupRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Items>> items) {
        // TODO Auto-generated method stub

    }

    public void itemSearch(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String xmlEscaping,
            String validate, ItemSearchRequest shared, List<ItemSearchRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Items>> items) {

        Items items2 = new Items();
        Item item = new Item();
        item.setASIN("my ASIN");
        items2.item.add(item);
        items.value.add(items2);

    }

    public void listLookup(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, ListLookupRequest shared, List<ListLookupRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Lists>> lists) {
        // TODO Auto-generated method stub

    }

    public void listSearch(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, ListSearchRequest shared, List<ListSearchRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Lists>> lists) {
        // TODO Auto-generated method stub

    }

    public void multiOperation(Help help, ItemSearch itemSearch, ItemLookup itemLookup, ListSearch listSearch, ListLookup listLookup,
            CustomerContentSearch customerContentSearch, CustomerContentLookup customerContentLookup, SimilarityLookup similarityLookup,
            SellerLookup sellerLookup, CartGet cartGet, CartAdd cartAdd, CartCreate cartCreate, CartModify cartModify, CartClear cartClear,
            TransactionLookup transactionLookup, SellerListingSearch sellerListingSearch, SellerListingLookup sellerListingLookup,
            TagLookup tagLookup, BrowseNodeLookup browseNodeLookup, Holder<OperationRequest> operationRequest,
            Holder<HelpResponse> helpResponse, Holder<ItemSearchResponse> itemSearchResponse,
            Holder<ItemLookupResponse> itemLookupResponse, Holder<ListSearchResponse> listSearchResponse,
            Holder<ListLookupResponse> listLookupResponse, Holder<CustomerContentSearchResponse> customerContentSearchResponse,
            Holder<CustomerContentLookupResponse> customerContentLookupResponse, Holder<SimilarityLookupResponse> similarityLookupResponse,
            Holder<SellerLookupResponse> sellerLookupResponse, Holder<CartGetResponse> cartGetResponse,
            Holder<CartAddResponse> cartAddResponse, Holder<CartCreateResponse> cartCreateResponse,
            Holder<CartModifyResponse> cartModifyResponse, Holder<CartClearResponse> cartClearResponse,
            Holder<TransactionLookupResponse> transactionLookupResponse, Holder<SellerListingSearchResponse> sellerListingSearchResponse,
            Holder<SellerListingLookupResponse> sellerListingLookupResponse, Holder<TagLookupResponse> tagLookupResponse,
            Holder<BrowseNodeLookupResponse> browseNodeLookupResponse) {
        // TODO Auto-generated method stub

    }

    public void sellerListingLookup(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag,
            String validate, String xmlEscaping, SellerListingLookupRequest shared, List<SellerListingLookupRequest> request,
            Holder<OperationRequest> operationRequest, Holder<List<SellerListings>> sellerListings) {
        // TODO Auto-generated method stub

    }

    public void sellerListingSearch(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag,
            String validate, String xmlEscaping, SellerListingSearchRequest shared, List<SellerListingSearchRequest> request,
            Holder<OperationRequest> operationRequest, Holder<List<SellerListings>> sellerListings) {
        // TODO Auto-generated method stub

    }

    public void sellerLookup(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, SellerLookupRequest shared, List<SellerLookupRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Sellers>> sellers) {
        // TODO Auto-generated method stub

    }

    public void similarityLookup(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag,
            String validate, String xmlEscaping, SimilarityLookupRequest shared, List<SimilarityLookupRequest> request,
            Holder<OperationRequest> operationRequest, Holder<List<Items>> items) {
        // TODO Auto-generated method stub

    }

    public void tagLookup(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag, String validate,
            String xmlEscaping, TagLookupRequest shared, List<TagLookupRequest> request, Holder<OperationRequest> operationRequest,
            Holder<List<Tags>> tags) {
        // TODO Auto-generated method stub

    }

    public void transactionLookup(String marketplaceDomain, String awsAccessKeyId, String subscriptionId, String associateTag,
            String validate, String xmlEscaping, TransactionLookupRequest shared, List<TransactionLookupRequest> request,
            Holder<OperationRequest> operationRequest, Holder<List<Transactions>> transactions) {
        // TODO Auto-generated method stub

    }

}
